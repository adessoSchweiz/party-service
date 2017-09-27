package ch.adesso.partyservice.configuration.boundary;

import ch.adesso.partyservice.AggregateProcessor;
import ch.adesso.partyservice.EventEnvelope;
import ch.adesso.partyservice.Topics;
import ch.adesso.partyservice.party.PartyEventStream;
import ch.adesso.partyservice.serializer.boundary.KafkaAvroReflectDeserializer;
import ch.adesso.partyservice.serializer.boundary.KafkaAvroReflectSerializer;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.common.serialization.Serdes.serdeFrom;
import static org.apache.kafka.streams.state.Stores.create;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaStreamProvider {

    public static final String SCHEMA_REGISTRY_URL = System.getenv("SCHEMA_REGISTRY_URL");

    public static final class PROPERTY_KEYS {
        public static final String APPLICATION_ID = "application.id";
        public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
        public static final String APPLICATION_SERVER = "application.server";
        public static final String STATE_DIR = "state.dir";
        public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";
        public static final String GROUP_ID = "group.id";
        public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
        public static final String COMMIT_INTERVAL_MS = "commit.interval.ms";
        public static final String PROCESSING_GUARANTEE = "processing.guarantee";
        public static final String NUM_STREAM_THREADS = "num.stream.threads";
    }

    private KafkaStreams kafkaStreams;

    @PostConstruct
    public void init() {
        this.kafkaStreams = createKafkaStreams();
    }

    @PreDestroy
    public void close() {
        this.kafkaStreams.close();
    }

    @Produces
    public KafkaStreams getKafkaStreams() {
        return kafkaStreams;
    }

    public KafkaStreams createKafkaStreams() {
        Properties properties = new Properties();
        properties.put(PROPERTY_KEYS.APPLICATION_ID, "streams-app");
        properties.put(PROPERTY_KEYS.BOOTSTRAP_SERVERS, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
        properties.put(PROPERTY_KEYS.APPLICATION_SERVER, System.getenv("APPLICATION_SERVER")); // localhost:8093
        properties.put(PROPERTY_KEYS.STATE_DIR, "/tmp/kafka-streams");
        properties.put(PROPERTY_KEYS.SCHEMA_REGISTRY_URL, SCHEMA_REGISTRY_URL); // http://avro-schema-registry:8081
        properties.put(PROPERTY_KEYS.GROUP_ID, "stream-app-group-1");
        properties.put(PROPERTY_KEYS.AUTO_OFFSET_RESET, "earliest");
        properties.put(PROPERTY_KEYS.COMMIT_INTERVAL_MS, 20);
        properties.put(PROPERTY_KEYS.PROCESSING_GUARANTEE, "exactly_once");
        properties.put(PROPERTY_KEYS.NUM_STREAM_THREADS, 10);

        KafkaStreams streams = new KafkaStreams(createStreamBuilder(), new StreamsConfig(properties));
        streams.cleanUp();
        streams.start();
        return streams;
    }

    public TopologyBuilder createStreamBuilder() {
        final Serde<EventEnvelope> eventSerde = createEventSerde();
        final Serde<PartyEventStream> eventStreamSerde = createStreamSerde();
        String sourceName = "party-events-source";
        String processorName = "party-processor";
        StateStoreSupplier<?> stateStore = createStateStore(eventStreamSerde);

        return new KStreamBuilder()
                .addStateStore(stateStore)
                .addSource(sourceName, new StringDeserializer(), eventSerde.deserializer(), Topics.PARTY_EVENTS_TOPIC.getTopic())
                .addProcessor(processorName,
                        () -> new AggregateProcessor(Topics.PARTY_STORE.getTopic()), sourceName)
                .connectProcessorAndStateStores(processorName, stateStore.name());
    }

    public StateStoreSupplier<?> createStateStore(Serde<PartyEventStream> eventStreamSerde) {
        return create(Topics.PARTY_STORE.getTopic())
                .withKeys(Serdes.String())
                .withValues(eventStreamSerde)
                .persistent()
                .enableCaching()
                .build();
    }

    public Serde<PartyEventStream> createStreamSerde() {
        final Serde<PartyEventStream> eventStreamSerde = serdeFrom(new KafkaAvroReflectSerializer<>(),
                new KafkaAvroReflectDeserializer<>(PartyEventStream.class));
        eventStreamSerde.configure(
                Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL),
                false);
        return eventStreamSerde;
    }

    public Serde<EventEnvelope> createEventSerde() {
        final Serde<EventEnvelope> eventSerde = serdeFrom(new KafkaAvroReflectSerializer<>(),
                new KafkaAvroReflectDeserializer<>(EventEnvelope.class));
        eventSerde.configure(
                Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL),
                false);
        return eventSerde;
    }

}
