package ch.adesso.partyservice.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import java.util.Properties;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaProvider {

    public static final String TOPIC = "person";
    public static final String KAFKA_ADDRESS = "kafka-1:29092,kafka-2:39092,kafka-3:49092";
    public static final String GROUP_ID = "party-gr";
    public static final String SCHEMA_REGISTRY_URL = "http://schema-registry:8081";

    private KafkaProducer<String, Object> producer;
    //private KafkaConsumer<String, String> consumer;

    @PostConstruct
    public void init() {
        this.producer = createProducer();
        //this.consumer = createConsumer();
    }

    @Produces
    public KafkaProducer<String, Object> getProducer() {
        return producer;
    }

    //@Produces
    //public KafkaConsumer<String, String> getConsumer() {
        //return consumer;
    //}

    public KafkaProducer<String, Object> createProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_ADDRESS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", KafkaAvroReflectSerializer.class);
        properties.put("schema.registry.url", SCHEMA_REGISTRY_URL);
        return new KafkaProducer<>(properties);
    }

    /*public KafkaConsumer<String, String> createConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_ADDRESS);
        properties.put("group.id", GROUP_ID + UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC));
        return consumer;
    }*/
}
