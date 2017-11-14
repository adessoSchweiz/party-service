package ch.adesso.teleport.persons.controller;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import ch.adesso.teleport.kafka.config.KafkaConfiguration;
import ch.adesso.teleport.kafka.config.Topics;
import ch.adesso.teleport.kafka.serializer.KafkaAvroReflectDeserializer;
import ch.adesso.teleport.kafka.serializer.KafkaAvroReflectSerializer;
import ch.adesso.teleport.kafka.serializer.KafkaJsonDeserializer;
import ch.adesso.teleport.kafka.serializer.KafkaJsonSerializer;
import ch.adesso.teleport.kafka.store.KafkaEventStore;
import ch.adesso.teleport.kafka.store.KafkaStoreProcessor;
import ch.adesso.teleport.kafka.store.ProcessedEvent;
import ch.adesso.teleport.persons.entity.Person;
import ch.adesso.teleport.persons.event.PersonEventEnvelope;

@Startup
@Singleton
public class PersonLocalStoreProvider {

	@Inject
	Event<ProcessedEvent> stateProcessedEvents;

	private KafkaStreams kafkaStreams;
	private KafkaEventStore kafkaLocalStore;

	@PostConstruct
	public void init() {

		Properties props = KafkaConfiguration.streamsDefaultProperties();
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "streams-persons");
		props.setProperty(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/persons");

		kafkaStreams = new KafkaStreams(createKafkaBuilder(), new StreamsConfig(props));
		kafkaStreams.cleanUp();
		kafkaStreams.start();

		kafkaLocalStore = new KafkaEventStore(kafkaStreams);

	}

	@PreDestroy
	public void close() {
		this.kafkaStreams.close();
	}

	public KafkaEventStore getKafkaLocalStore() {
		return kafkaLocalStore;
	}

	private TopologyBuilder createKafkaBuilder() {
		Serializer<PersonEventEnvelope> eventEnvelopeSerializer = new KafkaAvroReflectSerializer<>();
		Deserializer<PersonEventEnvelope> eventEnvelopeDeserializer = new KafkaAvroReflectDeserializer<>(
				PersonEventEnvelope.class);

		Serde<PersonEventEnvelope> eventEnvelopeSerde = Serdes.serdeFrom(eventEnvelopeSerializer,
				eventEnvelopeDeserializer);

		Serializer<Person> personSerializer = new KafkaJsonSerializer<>();
		Deserializer<Person> personDeserializer = new KafkaJsonDeserializer<>(Person.class);
		Serde<Person> personSerde = Serdes.serdeFrom(personSerializer, personDeserializer);

		String sourceTopic = Topics.PERSON_EVENT_TOPIC.toString();
		String storeName = Topics.PERSON_AGGREGATE_STORE.toString();

		String sourceName = sourceTopic + "-source";
		String processorName = storeName + "-processor";

		// local state store
		@SuppressWarnings("unchecked")
		StateStoreSupplier<KeyValueStore<String, Person>> stateStore = Stores.create(storeName)
				.withKeys(Serdes.String()).withValues(personSerde).persistent().build();

		// aggregate passenger events -> passenger
		return new TopologyBuilder().addStateStore(stateStore)
				.addSource(sourceName, Serdes.String().deserializer(), eventEnvelopeSerde.deserializer(), sourceTopic)
				.addProcessor(processorName,
						() -> new KafkaStoreProcessor<>(storeName, Person::new, stateProcessedEvents::fire), sourceName)
				.connectProcessorAndStateStores(processorName, storeName);

	}

}
