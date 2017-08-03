package ch.adesso.partyservice.party.controller;

import java.util.Collection;

import javax.inject.Inject;

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

public class KafkaStore {

	@Inject
	private KafkaProducer<String, Object> producer;

	@Inject
	private KafkaStreams kafkaStreams;

	public long publishEvents(String id,  long fromVersion, Collection<CoreEvent> events) {
		// producer.initTransactions();
		// producer.beginTransaction();
		try {
			long seq = fromVersion;
			for(CoreEvent e: events) {
				e.setSequence(seq);
				publishEvent(id, (PartyEvent)e); 
				seq++;
			};			
			
			producer.flush();

			seq--;
			
			return seq;
			
			// producer.commitTransaction();
		} catch (KafkaException e) {
			// producer.abortTransaction();
			throw e;
		}
	}

	public void publishEvent(String id, PartyEvent event) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PASSENGER_EVENTS_TOPIC.getTopic(), id,
				new EventEnvelope(event));
		producer.send(record);

	}

	public <T extends AggregateRoot> T findByIdAndVersion(String id, long expectedVersion) {
		T agggregateRoot = findById(id);
		if (agggregateRoot == null || expectedVersion > agggregateRoot.getVersion()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return findByIdAndVersion(id, expectedVersion);
		}
		return agggregateRoot;
	}

	public <T extends AggregateRoot> T findById(String id) {
		try {
			ReadOnlyKeyValueStore<String, T> store = QueryableStoreUtils.waitUntilStoreIsQueryable(
					Topics.PASSENGER_STORE.getTopic(), QueryableStoreTypes.<String, T>keyValueStore(), kafkaStreams);
			T agggregateRoot = store.get(id);
			return agggregateRoot;
		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}
	}

}
