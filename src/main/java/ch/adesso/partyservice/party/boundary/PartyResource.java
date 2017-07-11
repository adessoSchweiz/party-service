package ch.adesso.partyservice.party.boundary;

import ch.adesso.partyservice.kafka.KafkaProvider;
import ch.adesso.partyservice.party.entity.EventEnvelope;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.entity.PersonCreatedEvent;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Path("parties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PartyResource {

    @Inject
    private KafkaProducer<String, Object> producer;

    @POST
    public Person createPerson(Person person) throws InterruptedException, ExecutionException, TimeoutException {
        UUID uuid = UUID.randomUUID();
        person.setId(uuid.toString());

        PersonCreatedEvent e = new PersonCreatedEvent(uuid.toString(), person.getFirstname(), person.getLastname());


        Schema schema = ReflectData.get().getSchema(EventEnvelope.class);

        System.out.println(schema.toString(true));

        ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaProvider.TOPIC,
                person.getId(), new EventEnvelope(e));
        Future<RecordMetadata> md = producer.send(record);
        producer.flush();

        RecordMetadata rnd = md.get(1l, TimeUnit.SECONDS);
        System.out.println("Person: " + person.toString() + " and offset: " + rnd.offset());
        person.setVersion(rnd.offset());
        return person;
    }

    private void checkAvro(EventEnvelope evnvelope) throws IOException {
        Schema schema = ReflectData.get().getSchema(EventEnvelope.class);

        System.out.println(schema.toString(true));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder e = EncoderFactory.get().binaryEncoder(outputStream, null);
        // Encoder e = EncoderFactory.get().jsonEncoder(schema, os );

        DatumWriter<EventEnvelope> writer = new ReflectDatumWriter<EventEnvelope>(schema);
        PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent("1", "name", "lastname");
        EventEnvelope envelope = new EventEnvelope(personCreatedEvent);
        System.out.println(envelope.toString());

        writer.write(envelope, e);
        e.flush();
    }
}
