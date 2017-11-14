package ch.adesso.teleport.kafka.config;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Options;

import ch.adesso.teleport.kafka.serializer.EventEnvelopeAvroDeserializer;
import ch.adesso.teleport.kafka.serializer.KafkaAvroReflectSerializer;

public class KafkaConfiguration {

	public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";

	public static Properties producerDefaultProperties() {
		Properties properties = new Properties();
		properties.put(SCHEMA_REGISTRY_URL, System.getenv("SCHEMA_REGISTRY_URL")); // http://avro-schema-registry:8081
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroReflectSerializer.class.getName());
		properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
		properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

		return properties;
	}

	public static Properties consumerDefaultProperties() {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
		properties.put(SCHEMA_REGISTRY_URL, System.getenv("SCHEMA_REGISTRY_URL")); // http://avro-schema-registry:8081
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventEnvelopeAvroDeserializer.class);
		properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group" + UUID.randomUUID());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		return properties;
	}

	public static Properties streamsDefaultProperties() {
		Properties properties = new Properties();
		// application.id is used to set client.id, group.id and state.dir, see Kafka
		// Docs.
		properties.put(SCHEMA_REGISTRY_URL.toString(), System.getenv("SCHEMA_REGISTRY_URL")); // http://avro-schema-registry:8081
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS")); // kafka:9092
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
		properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, System.getenv("APPLICATION_SERVER")); // localhost:8093
		properties.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once");
		properties.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
		properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 20);

		properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG,
				Math.max(Runtime.getRuntime().availableProcessors(), 2));

		// properties.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG,
		// CustomRocksDBConfig.class);

		properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
		properties.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 500);

		return properties;
	}

	public static class CustomRocksDBConfig implements RocksDBConfigSetter {

		// https://github.com/apache/kafka/blob/0.10.2/streams/src/main/java/org/apache/kafka/streams/state/internals/RocksDBStore.java#L117
		@Override
		public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
			BlockBasedTableConfig tableConfig = new org.rocksdb.BlockBasedTableConfig();

			tableConfig.setBlockCacheSize(100 * 1024 * 1024L);

			options.setTableFormatConfig(tableConfig);
		}
	}

}
