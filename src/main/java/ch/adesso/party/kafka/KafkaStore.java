package ch.adesso.party.kafka;

import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

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

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ch.adesso.party.AggregateRoot;
import ch.adesso.party.CoreEvent;
import ch.adesso.party.EventEnvelope;
import ch.adesso.party.event.PartyEvent;
import kafka.common.KafkaException;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class KafkaStore {

	@Inject
	KafkaProducer<String, Object> producer;

	@Inject
	KafkaStreams kafkaStreams;

	@Inject
	Event<CoreEvent> publishedEvents;

	public <T extends AggregateRoot> void save(T aggregateRoot) {
		Collection<CoreEvent> events = aggregateRoot.getUncommitedEvents();
		publishEvents(events);
		aggregateRoot.clearEvents();

		events.forEach(publishedEvents::fire);
	}

	public <T extends AggregateRoot> T findById(String id, Function<JsonObject, T> aggregateFactory) {
		T root = loadAggregateFromLocalStore(id, Topics.PARTY_STORE.getTopic(), aggregateFactory);
		if (root == null) {
			throw new EntityNotFoundException("Could not find Entity for ID: " + id);
		}

		return (T) root;
	}

	public <T extends AggregateRoot> T findByIdAndVersionWaitForResult(T aggregate,
			Function<JsonObject, T> aggregateFactory) {
		int loop = 0;
		String id = aggregate.getId();
		long version = aggregate.getVersion();
		while (true) {
			AggregateRoot root = loadAggregateFromLocalStore(id, Topics.PARTY_STORE.getTopic(), aggregateFactory);
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

		throw new EntityNotFoundException("Could not find Entity for ID: " + id + " and version: " + version);
	}

	private void publishEvents(Collection<CoreEvent> events) {
		List<CompletableFuture<RecordMetadata>> futures = new ArrayList<>();
		producer.beginTransaction();
		try {
			for (CoreEvent e : events) {
				futures.add(publishEvent((PartyEvent) e));
			}
			waitForAll(futures);
			producer.commitTransaction();

		} catch (ProducerFencedException fEx) {
			producer.abortTransaction();
		} catch (KafkaException kEx) {
			producer.close();
		}
	}

	private CompletableFuture<RecordMetadata> publishEvent(PartyEvent event) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(Topics.PARTY_EVENTS_TOPIC.getTopic(),
				event.getAggregateId(), new EventEnvelope(event));

		CompletableFuture<RecordMetadata> f = new CompletableFuture<>();
		producer.send(record, (metadata, exception) -> {
			if (exception != null) {
				f.completeExceptionally(exception);
			} else {
				f.complete(metadata);
			}
		});

		return f;
	}

	private <T extends AggregateRoot> T loadAggregateFromLocalStore(String aggregateId, String storeName,
			Function<JsonObject, T> aggregateFactory) {
		ReadOnlyKeyValueStore<String, String> store = null;
		try {
			store = QueryableStoreUtils.waitUntilStoreIsQueryable(storeName, keyValueStore(), kafkaStreams);

		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}

		String jsonString = store.get(aggregateId);
		if (jsonString != null) {
			JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			return aggregateFactory.apply(jsonObject);
		}

		return null;
	}

	private static <T> CompletableFuture<List<T>> waitForAll(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture
				.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
	}
}