package ch.adesso.partyservice.eventstore.boundary;

import ch.adesso.partyservice.*;
import ch.adesso.partyservice.party.PartyEvent;
import ch.adesso.partyservice.party.passenger.entity.Passenger;
import kafka.common.KafkaException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaStore {

    @Inject
    KafkaProducer<String, Object> producer;

    @Inject
    KafkaStreams kafkaStreams;

    @Inject
    Event<CoreEvent> events;

    public void publishEvents(Collection<CoreEvent> events) {
        List<CompletableFuture<RecordMetadata>> futures = new ArrayList<>();
        for (CoreEvent e : events) {
            futures.add(publishEvent((PartyEvent) e));
        }
        waitForAll(futures);
        producer.flush();
    }

    public CompletableFuture<RecordMetadata> publishEvent(PartyEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PARTY_EVENTS_TOPIC.getTopic(),
                event.getAggregateId(), new EventEnvelope(event));

        CompletableFuture<RecordMetadata> f = new CompletableFuture<>();
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                f.completeExceptionally(exception);
            } else {
                f.complete(metadata);
                events.fire(event);
            }
        });

        return f;
    }

    public AggregateRoot getAggregateRoot(String aggregateId, String storeName) {
        ReadOnlyKeyValueStore<String, String> store = null;
        try {
            store = QueryableStoreUtils.waitUntilStoreIsQueryable(
                    storeName, keyValueStore(), kafkaStreams);

        } catch (InterruptedException e) {
            throw new KafkaException("KeyValueStore can not read current data.", e);
        }

        String jsonString = store.get(aggregateId);
        if (jsonString != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            return new Passenger(jsonObject);
        }

        return null;
    }

    public <T extends AggregateRoot> T findById(String id, Class<T> partyClass) {
        AggregateRoot root = getAggregateRoot(id, Topics.PARTY_STORE.getTopic());
        if (root == null) {
            throw new EntityNotFoundException("Could not find Entity for ID: " + id);
        }

        return (T) root;
    }

    public <T extends AggregateRoot> T findByIdAndVersionWaitForResult(String id, long version, Class<T> partyClass) {
        int loop = 0;
        while (true) {
            AggregateRoot root = getAggregateRoot(id, Topics.PARTY_STORE.getTopic());
            if (root == null || (root.getVersion() != version)) {
                loop++;
                if (loop > 20) {
                    break;
                }
                try {
                    Thread.sleep(20L);
                    continue;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    break;
                }

            }

            return (T) root;
        }

        throw new EntityNotFoundException("Could not find Entity for ID: " + id);
    }

    private static <T> CompletableFuture<List<T>> waitForAll(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture
                .thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
    }
}
