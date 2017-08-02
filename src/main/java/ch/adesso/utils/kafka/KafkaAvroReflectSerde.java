package ch.adesso.utils.kafka;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Collections;
import java.util.Map;

public class KafkaAvroReflectSerde<T> implements Serde<T> {

  private final Serde<T> inner;

  /**
   * Constructor used by Kafka Streams.
   */
  public KafkaAvroReflectSerde() {
    inner = Serdes.serdeFrom(new KafkaAvroReflectSerializer<T>(), new KafkaAvroReflectDeserializer<T>());
  }

  public KafkaAvroReflectSerde(SchemaRegistryClient client) {
    this(client, Collections.emptyMap());
  }

  public KafkaAvroReflectSerde(SchemaRegistryClient client, Map<String, ?> props) {
    inner = Serdes.serdeFrom(new KafkaAvroReflectSerializer<T>(client, props), new KafkaAvroReflectDeserializer<T>(client, props));
  }

  @Override
  public Serializer<T> serializer() {
    return inner.serializer();
  }

  @Override
  public Deserializer<T> deserializer() {
    return inner.deserializer();
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    inner.serializer().configure(configs, isKey);
    inner.deserializer().configure(configs, isKey);
  }

  @Override
  public void close() {
    inner.serializer().close();
    inner.deserializer().close();
  }

}