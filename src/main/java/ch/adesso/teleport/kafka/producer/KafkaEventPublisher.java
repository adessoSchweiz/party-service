package ch.adesso.teleport.kafka.producer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import ch.adesso.teleport.AggregateRoot;
import ch.adesso.teleport.CoreEvent;
import ch.adesso.teleport.EventEnvelope;

public class KafkaEventPublisher {

	private KafkaProducer<String, EventEnvelope<? extends CoreEvent>> producer;
	Function<CoreEvent, EventEnvelope<? extends CoreEvent>> envelopeFactory;// = (e -> new
																			// PersonEventEnvelope((PersonEvent) e));

	private Consumer<PublishedEvent> producerEvents;

	public KafkaEventPublisher(KafkaProducer<String, EventEnvelope<? extends CoreEvent>> producer,
			Function<CoreEvent, EventEnvelope<? extends CoreEvent>> envelopeFactory) {
		this.producer = producer;
		this.envelopeFactory = envelopeFactory;
	}

	public KafkaEventPublisher(KafkaProducer<String, EventEnvelope<? extends CoreEvent>> producer,
			Function<CoreEvent, EventEnvelope<? extends CoreEvent>> envelopeFactory,
			Consumer<PublishedEvent> producerEvents) {
		this(producer, envelopeFactory);
		this.producerEvents = producerEvents;
	}

	public <T extends AggregateRoot> void save(String topicName, T aggregateRoot) {
		Collection<? extends CoreEvent> events = aggregateRoot.getUncommitedEvents();
		publishEvents(topicName, events);
		if (producerEvents != null) {
			events.forEach(e -> producerEvents.accept(new PublishedEvent(e)));
		}
		aggregateRoot.clearEvents();
	}

	private void publishEvents(String topicName, Collection<? extends CoreEvent> events) {
		List<CompletableFuture<RecordMetadata>> futures = new ArrayList<>();
		events.stream().forEach(e -> futures.add(publishEvent(topicName, e)));
		waitForAll(futures);
		producer.flush();
	}

	private <E extends CoreEvent> CompletableFuture<RecordMetadata> publishEvent(String topicName, E event) {
		String aggregateId = event.getAggregateId();
		ProducerRecord<String, EventEnvelope<? extends CoreEvent>> record = new ProducerRecord<>(topicName, aggregateId,
				envelopeFactory.apply(event));

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

	private static <T> CompletableFuture<List<T>> waitForAll(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture
				.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
	}
}
