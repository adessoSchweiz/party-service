package ch.adesso.partyservice.party.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ch.adesso.partyservice.party.entity.AggregateRoot;
import ch.adesso.partyservice.party.entity.PartyEventStream;
import ch.adesso.partyservice.person.entity.Person;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.EventEnvelope;
import ch.adesso.partyservice.party.event.PartyEvent;
import ch.adesso.partyservice.party.kafka.Topics;
import ch.adesso.utils.kafka.QueryableStoreUtils;
import kafka.common.KafkaException;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaStore {

	@Inject
	private KafkaProducer<String, Object> producer;

	@Inject
	private KafkaStreams kafkaStreams;

	@Inject
	Event<CoreEvent> events;

	@PostConstruct
	public void init() {
		// producer.initTransactions();
	}

	public void publishEvents(Collection<CoreEvent> events) {
		try {
			// producer.beginTransaction();

			List<CompletableFuture<RecordMetadata>> futures = new ArrayList<CompletableFuture<RecordMetadata>>();
			for (CoreEvent e : events) {
				futures.add(publishEvent((PartyEvent) e));
			}

			waitForAll(futures).exceptionally(ex -> {
				ex.printStackTrace();
				return null;
			});

			producer.flush();

			// producer.commitTransaction();

		} catch (KafkaException e) {
			// producer.abortTransaction();
			throw e;
		}
	}

	public CompletableFuture<RecordMetadata> publishEvent(PartyEvent event) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PARTY_EVENTS_TOPIC.getTopic(),
				event.getAggregateId(), new EventEnvelope(event));

		CompletableFuture<RecordMetadata> f = new CompletableFuture<RecordMetadata>();
		producer.send(record, new Callback() {

			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					f.completeExceptionally(exception);
				} else {
					f.complete(metadata);
					events.fire(event);
				}
			}
		});

		return f;
	}

	public <T extends AggregateRoot> T findByIdAndVersion(String id, long version, Class<T> partyClazz) {

		T aggregateRoot = (T) findById(id, partyClazz);
		if (aggregateRoot != null && aggregateRoot.getVersion() == version) {
			return aggregateRoot;
		}

		throw new EntityNotFoundException("Could not find Entity for ID: " + id + ", and Version: " + version);
	}

	public PartyEventStream loadLastEvents(String aggregateId, String storeName) {
		try {
			ReadOnlyKeyValueStore<String, PartyEventStream> store = QueryableStoreUtils.waitUntilStoreIsQueryable(
					storeName, QueryableStoreTypes.<String, PartyEventStream>keyValueStore(), kafkaStreams);

			PartyEventStream stream = store.get(aggregateId);
			return stream;

		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}
	}

	public <T extends AggregateRoot> T findById(String id, Class<T> partyClass) {
		PartyEventStream stream = loadLastEvents(id, Topics.PARTY_STORE.getTopic());
		if (stream == null) {
			throw new EntityNotFoundException("Could not find Entity for ID: " + id);
		}

		try {
			T party = partyClass.newInstance();
			stream.getLastEvents().values().stream()
					.sorted((e1, e2) -> Long.compare(e1.getSequence(), e2.getSequence())).forEach(party::applyEvent);

			return party;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public <T extends AggregateRoot> T findByIdAndVersionWaitForResul(String id, long version, Class<T> partyClass) {
		int loop = 0;
		while (true) {
			PartyEventStream stream = loadLastEvents(id, Topics.PARTY_STORE.getTopic());
			if (stream == null || (stream.getAggregateVersion() != version)) {
				loop++;
				if (loop > 20) {
					break;
				}
				try {
					Thread.sleep(20);
					continue;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}

			} else {

				try {
					T party = partyClass.newInstance();
					stream.getLastEvents().values().stream()
							.sorted((e1, e2) -> Long.compare(e1.getSequence(), e2.getSequence()))
							.forEach(party::applyEvent);

					return party;
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		throw new EntityNotFoundException("Could not find Entity for ID: " + id);
	}

	public Person findByCredentials(String login, String password) {
		PartyEventStream stream = loadLastEvents(login, Topics.PARTY_LOGIN_STORE.getTopic());
		if (stream == null) {
			throw new EntityNotFoundException("Could not find Person for login: " + login);
		}

		Person person = new Person();
		stream.getLastEvents().values().stream().sorted((e1, e2) -> Long.compare(e1.getSequence(), e2.getSequence()))
				.forEach(person::applyEvent);

		return person;
	}

	private static <T> CompletableFuture<List<T>> waitForAll(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture
				.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
	}
}
