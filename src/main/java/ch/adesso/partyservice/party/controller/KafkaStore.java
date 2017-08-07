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

	public long publishEvents(String id, long fromVersion, Collection<CoreEvent> events) {
		try {
			// producer.beginTransaction();

			long seq = fromVersion;
			for (CoreEvent e : events) {
				e.setSequence(seq);
				publishEvent(id, (PartyEvent) e);
				seq++;
			}

			producer.flush();

			// producer.commitTransaction();

			seq--;
			return seq;

		} catch (KafkaException e) {
			// producer.abortTransaction();
			throw e;
		}
	}

	public void publishEvent(String id, PartyEvent event) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PASSENGER_EVENTS_TOPIC.getTopic(), id,
				new EventEnvelope(event));

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

	public <T extends AggregateRoot> T findByIdAndVersion(String id, long expectedVersion) {

		T aggregateRoot = null;
		int loop = 0;
		int elapsedTime = 0;
		while (loop < 5) {
			aggregateRoot = findById(id);
			if (aggregateRoot != null && aggregateRoot.getVersion() == expectedVersion) {
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
				"Could not find entity: " + id + ", version: " + expectedVersion + ", elapsed time: " + elapsedTime);
	}

	public <T extends AggregateRoot> T findById(String id) {
		try {
			ReadOnlyKeyValueStore<String, T> store = QueryableStoreUtils.waitUntilStoreIsQueryable(
					Topics.PASSENGER_STORE.getTopic(), QueryableStoreTypes.<String, T>keyValueStore(), kafkaStreams);

			return store.get(id);

		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}
	}

	public <T extends AggregateRoot> T findByCredentials(String login, String password) {
		try {
			ReadOnlyKeyValueStore<String, T> store = QueryableStoreUtils.waitUntilStoreIsQueryable(
					Topics.PASSENGER_LOGIN_STORE.getTopic(), QueryableStoreTypes.<String, T>keyValueStore(),
					kafkaStreams);

			return store.get(login + password);

		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}
	}

}
