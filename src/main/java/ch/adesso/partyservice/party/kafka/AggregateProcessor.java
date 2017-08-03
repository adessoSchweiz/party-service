package ch.adesso.partyservice.party.kafka;

import java.util.function.Supplier;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.entity.AggregateRoot;
import ch.adesso.partyservice.party.event.EventEnvelope;

public class AggregateProcessor<T extends AggregateRoot> implements Processor<String, EventEnvelope> {

	private String storeName;
	Supplier<T> aggregateRootProvider;
	private ProcessorContext context;
	private KeyValueStore<String, T> kvStore;

	public AggregateProcessor(String storeName, Supplier<T> aggregateRootProvider) {
		this.storeName = storeName;
		this.aggregateRootProvider = aggregateRootProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvStore = (KeyValueStore<String, T>) context.getStateStore(storeName);
	}

	@Override
	public void process(String key, EventEnvelope value) {
		T aggregateRoot = kvStore.get(key);
		if (aggregateRoot == null) {
			aggregateRoot = aggregateRootProvider.get();
		}
		
		aggregateRoot.applyEvent(value.getEvent());
		kvStore.put(key, aggregateRoot);

		context.commit();
	}

	@Override
	public void punctuate(long timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		kvStore.close();
	}

}
