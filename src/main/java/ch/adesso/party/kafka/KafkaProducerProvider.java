package ch.adesso.party.kafka;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

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
		public static final String TRANSACTIONAL_ID = "transactional.id";
	}

	private KafkaProducer<String, Object> producer;

	@PostConstruct
	public void init() {
		this.producer = createProducer();
		this.producer.initTransactions();
	}

	@Produces
	public KafkaProducer<String, Object> getProducer() {
		return producer;
	}

	public KafkaProducer<String, Object> createProducer() {
		Properties properties = new Properties();
		properties.put(PROPERTY_KEYS.BOOTSTRAP_SERVERS, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
		properties.put(PROPERTY_KEYS.SCHEMA_REGISTRY_URL, System.getenv("SCHEMA_REGISTRY_URL")); // http://avro-schema-registry:8081
		properties.put(PROPERTY_KEYS.KEY_SERIALIZER, StringSerializer.class.getName());
		properties.put(PROPERTY_KEYS.VALUE_SERIALIZER, KafkaAvroReflectSerializer.class.getName());
		properties.put(PROPERTY_KEYS.LINGER_MS, 10);
		properties.put(PROPERTY_KEYS.ENABLE_IDEMPOTENCE, true);
		properties.put(PROPERTY_KEYS.TRANSACTIONAL_ID, "transactionalId-1");
		return new KafkaProducer<>(properties);
	}

}