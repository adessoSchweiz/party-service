package ch.adesso.utils.kafka;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class KafkaAvroReflectDeserializer<T> extends AbstractKafkaAvroDeserializer implements Deserializer<T> {

    private Schema readerSchema;
    private DecoderFactory decoderFactory = DecoderFactory.get();

    public KafkaAvroReflectDeserializer(Class<T> type) {
        readerSchema = ReflectData.get().getSchema(type);
    }

    @SuppressWarnings("unchecked")
	public KafkaAvroReflectDeserializer() {
        Type t = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        readerSchema = ReflectData.get().getSchema((Class<T>)t);
    }

    public KafkaAvroReflectDeserializer(SchemaRegistryClient client) {
        this();
        this.schemaRegistry = client;
    }

    public KafkaAvroReflectDeserializer(SchemaRegistryClient client, Map<String, ?> props) {
        this(client);
        this.configure(this.deserializerConfig(props));
    }


    public void configure(Map<String, ?> configs, boolean isKey) {
        this.configure(new KafkaAvroDeserializerConfig(configs));
    }

    @SuppressWarnings("unchecked")
	public T deserialize(String s, byte[] bytes) {
        return (T)this.deserialize(bytes);
    }

    @SuppressWarnings("unchecked")
	public T deserialize(String s, byte[] bytes, Schema readerSchema) {
        return (T)this.deserialize(bytes, readerSchema);
    }

    public void close() {
    }

    @SuppressWarnings("unchecked")
	@Override
    protected T deserialize(
            boolean includeSchemaAndVersion,
            String topic,
            Boolean isKey,
            byte[] payload,
            Schema readerSchemaIgnored) throws SerializationException {

        if (payload == null) {
            return null;
        }

        int schemaId = -1;
        try {
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            if (buffer.get() != MAGIC_BYTE) {
                throw new SerializationException("Unknown magic byte!");
            }

            schemaId = buffer.getInt();
            Schema writerSchema = schemaRegistry.getById(schemaId);

            int start = buffer.position() + buffer.arrayOffset();
            int length = buffer.limit() - 1 - idSize;
            DatumReader<Object> reader = new ReflectDatumReader<Object>(writerSchema, readerSchema);
            BinaryDecoder decoder = decoderFactory.binaryDecoder(buffer.array(), start, length, null);
            return (T) reader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing Avro message for id " + schemaId, e);
        } catch (RestClientException e) {
            throw new SerializationException("Error retrieving Avro schema for id " + schemaId, e);
        }
    }
}
