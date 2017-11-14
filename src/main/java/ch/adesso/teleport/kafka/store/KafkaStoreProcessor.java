package ch.adesso.teleport.kafka.store;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.teleport.AggregateRoot;
import ch.adesso.teleport.CoreEvent;
import ch.adesso.teleport.EventEnvelope;

public class KafkaStoreProcessor<T extends AggregateRoot>
		implements Processor<String, EventEnvelope<? extends CoreEvent>> {

	private String storeName;
	private ProcessorContext context;
	private KeyValueStore<String, T> kvStore;
	private Consumer<ProcessedEvent> eventConsumer;
	private Supplier<T> aggregateFactory;

	public KafkaStoreProcessor(String storeName, Supplier<T> aggregateFactory, Consumer<ProcessedEvent> eventConsumer) {
		this.storeName = storeName;
		this.eventConsumer = eventConsumer;
		this.aggregateFactory = aggregateFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvStore = (KeyValueStore<String, T>) context.getStateStore(storeName);
	}

	@Override
	public void process(String key, EventEnvelope<? extends CoreEvent> eventEnv) {

		T aggregate = kvStore.get(key);
		if (aggregate == null) {
			aggregate = aggregateFactory.get();
		}

		aggregate.applyEvent(eventEnv.getEvent());

		kvStore.put(key, aggregate);
		if (eventConsumer != null) {
			eventConsumer.accept(new ProcessedEvent(eventEnv.getEvent()));
		}

		context.forward(key, aggregate);
		context.commit();
	}

	@Override
	public void punctuate(long timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// https://issues.apache.org/jira/browse/KAFKA-4919
	}

}
