package ch.adesso.partyservice.party.controller;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ch.adesso.partyservice.party.entity.AggregateRoot;
import ch.adesso.partyservice.party.entity.PartyEventStream;
import ch.adesso.partyservice.party.entity.Person;
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

	private int readMilis = 100;

	@Inject
	private KafkaProducer<String, Object> producer;

	@Inject
	private KafkaStreams kafkaStreams;

	@PostConstruct
	public void init() {
		// producer.initTransactions();
	}

	public void publishEvents(Collection<CoreEvent> events) {
		try {
			// producer.beginTransaction();

			for (CoreEvent e : events) {
				publishEvent((PartyEvent) e);
			}

			producer.flush();

			// producer.commitTransaction();

		} catch (KafkaException e) {
			// producer.abortTransaction();
			throw e;
		}
	}

	public void publishEvent(PartyEvent event) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PARTY_EVENTS_TOPIC.getTopic(),
				event.getAggregateId(), new EventEnvelope(event));

		// Future<RecordMetadata> mdf =
		producer.send(record);

		// try {
		// RecordMetadata md = mdf.get();
		// System.out.println("Record send, offset: " + md.offset());
		// } catch (InterruptedException | ExecutionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public <T extends AggregateRoot> T findByIdAndVersion(String id, long version, Class<T> partyClazz) {

		T aggregateRoot = null;
		int loop = 0;
		int elapsedTime = 0;
		while (loop < 5) {
			aggregateRoot = (T) findById(id, partyClazz);
			if (aggregateRoot != null && aggregateRoot.getVersion() == version) {
				return aggregateRoot;
			} else {
				try {
					loop = loop + 1;
					Thread.sleep(readMilis * loop);
					elapsedTime = elapsedTime + readMilis * loop;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (aggregateRoot != null) {
			System.out.println("AggregateRoot found but wrong version: " + aggregateRoot);
		}
		throw new EntityNotFoundException(
				"Could not find entity: " + id + ", version: " + version + ", elapsed time: " + elapsedTime);
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
			return null;
		}

		T party;
		try {
			party = partyClass.newInstance();
			stream.getLastEvents().values().stream()
					.sorted((e1, e2) -> Long.compare(e1.getSequence(), e2.getSequence())).forEach(party::applyEvent);

			return party;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Person findByCredentials(String login, String password) {
		PartyEventStream stream = loadLastEvents(login, Topics.PARTY_LOGIN_STORE.getTopic());
		if (stream == null) {
			return null;
		}

		Person person = new Person();
		stream.getLastEvents().values().stream().sorted((e1, e2) -> Long.compare(e1.getSequence(), e2.getSequence()))
				.forEach(person::applyEvent);

		return person;
	}

}
