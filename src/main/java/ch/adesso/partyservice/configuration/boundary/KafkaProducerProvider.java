package ch.adesso.partyservice.configuration.boundary;

import org.apache.kafka.clients.producer.KafkaProducer;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import java.util.Properties;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaProducerProvider {

    public static final class PROPERTY_KEYS {
        public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
        public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";
        public static final String KEY_SERIALIZER = "key.serializer";
        public static final String VALUE_SERIALIZER = "value.serializer";
        public static final String LINGER_MS = "linger.ms";
        public static final String ENABLE_IDEMPOTENCE = "enable.idempotence";
    }

    private KafkaProducer<String, Object> producer;

    @PostConstruct
    public void init() {
        this.producer = createProducer();
    }

    @Produces
    public KafkaProducer<String, Object> getProducer() {
        return producer;
    }

    public KafkaProducer<String, Object> createProducer() {
        Properties properties = new Properties();
        properties.put(PROPERTY_KEYS.BOOTSTRAP_SERVERS, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
        properties.put(PROPERTY_KEYS.SCHEMA_REGISTRY_URL, System.getenv("SCHEMA_REGISTRY_URL")); // http://avro-schema-registry:8081
        properties.put(PROPERTY_KEYS.KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(PROPERTY_KEYS.VALUE_SERIALIZER, "ch.adesso.partyservice.serializer.boundary.KafkaAvroReflectSerializer");
        properties.put(PROPERTY_KEYS.LINGER_MS, 10);
        properties.put(PROPERTY_KEYS.ENABLE_IDEMPOTENCE, true);
        return new KafkaProducer<>(properties);
    }

}
