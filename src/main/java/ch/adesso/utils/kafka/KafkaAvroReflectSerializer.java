package ch.adesso.utils.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

/**
 * Extension of KafkaAvroSerializer that supports reflection-based serialization of values when
 * objects are wrapped in a ReflectContainer.
 */
public class KafkaAvroReflectSerializer<T>  extends AbstractKafkaAvroSerializer implements Serializer<T> {

    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();
    private static final ReflectData REFLECT_DATA = ReflectData.get();

    private boolean isKey;

    public KafkaAvroReflectSerializer() {
    }

    public KafkaAvroReflectSerializer(SchemaRegistryClient client) {
        this.schemaRegistry = client;
    }

    public KafkaAvroReflectSerializer(SchemaRegistryClient client, Map<String, ?> props) {
        this.schemaRegistry = client;
        this.configure(this.serializerConfig(props));
    }

    public void configure(Map<String, ?> configs, boolean isKey) {
        this.isKey = isKey;
        this.configure(new KafkaAvroSerializerConfig(configs));
    }

    public byte[] serialize(String topic, T record) {
        return this.serializeImpl(getSubjectName(topic, this.isKey), record);
    }

    public void close() {
    }

    @Override
    protected byte[] serializeImpl(String subject, Object object) throws SerializationException {

        if (object == null) {
            return null;
        } else {
            Schema schema = REFLECT_DATA.getSchema(object.getClass());
            try {
                int registeredSchemaId = this.schemaRegistry.register(subject, schema);

                System.out.println("Registered Schema: " + registeredSchemaId);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(0);
                out.write(ByteBuffer.allocate(4).putInt(registeredSchemaId).array());
                if (object instanceof byte[]) {
                    out.write((byte[]) ((byte[]) object));
                } else {
                    DatumWriter<Object> dw = new ReflectDatumWriter<>(schema);
                    Encoder encoder = ENCODER_FACTORY.directBinaryEncoder(out, null);
                    dw.write(object, encoder);
                    encoder.flush();
                }
                byte[] bytes = out.toByteArray();
                out.close();
                return bytes;
            } catch (RuntimeException | IOException e) {
                throw new SerializationException("Error serializing Avro message", e);
            } catch (RestClientException e) {
                throw new SerializationException("Error registering Avro schema: " + schema, e);
            }
        }
    }
}