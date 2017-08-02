package ch.adesso.partyservice.party.controller;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ch.adesso.partyservice.party.entity.EventEnvelope;
import ch.adesso.partyservice.party.entity.PartyEvent;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.kafka.Topics;
import ch.adesso.utils.kafka.QueryableStoreUtils;
import kafka.common.KafkaException;

public class KafkaStore {

	@Inject
	private KafkaProducer<String, Object> producer;

	@Inject
	private KafkaStreams kafkaStreams;
	
	public void publishEvents(String id, Collection<PartyEvent> events) {
		// producer.initTransactions();
		// producer.beginTransaction();
		try {
			events.forEach(e -> publishEvent(id, e));
			producer.flush();
			// producer.commitTransaction();
		} catch (KafkaException e) {
			// producer.abortTransaction();
			throw e;
		}
	}

	public void publishEvent(String id, PartyEvent event) {
		Schema schema = ReflectData.get().getSchema(EventEnvelope.class);

		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PARTY_EVENTS_TOPIC.getTopic(), id,
				new EventEnvelope(event));
		producer.send(record);

	}
	
	public Person findByIdAndVersion(String id, long expectedVersion) {
		Person p = findById(id);
		if(p == null || expectedVersion > p.getVersion()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return findByIdAndVersion(id, expectedVersion);
		}
		return p;
	}

	public Person findById(String id) {
		try {
			ReadOnlyKeyValueStore<String, Person> store = QueryableStoreUtils.waitUntilStoreIsQueryable(
					Topics.PARTY_STORE.getTopic(), QueryableStoreTypes.<String, Person>keyValueStore(), kafkaStreams);
			Person person = store.get(id);
			return person;
		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}
	}
	
}
