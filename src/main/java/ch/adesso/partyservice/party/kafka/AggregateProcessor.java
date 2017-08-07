package ch.adesso.partyservice.party.kafka;

import java.util.function.Supplier;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.entity.AggregateRoot;
import ch.adesso.partyservice.party.entity.AggregateRootFactory;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.EventEnvelope;

public class AggregateProcessor<T extends AggregateRoot> implements Processor<String, EventEnvelope> {

	private String storeName;
	Supplier<AggregateRootFactory<T>> aggregateRootFactoryProvider;
	private ProcessorContext context;
	private KeyValueStore<String, T> kvStore;

	public AggregateProcessor(String storeName, Supplier<AggregateRootFactory<T>> aggregateRootFactoryProvider) {
		this.storeName = storeName;
		this.aggregateRootFactoryProvider = aggregateRootFactoryProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvStore = (KeyValueStore<String, T>) context.getStateStore(storeName);
	}

	@Override
	public void process(String key, EventEnvelope value) {
		CoreEvent event = value.getEvent();
		T aggregateRoot = kvStore.get(key);
		if (aggregateRoot == null) {
			aggregateRoot = aggregateRootFactoryProvider.get().newInstance(event.getAggregateId());
		}

		int loopCount = 0;
		while (loopCount < 5) {
			try {
				System.out.println("apply event: " + value.getEvent());
				aggregateRoot.applyEvent(value.getEvent());

				kvStore.put(key, aggregateRoot);
				kvStore.flush();

				context.commit();

				break;
			} catch (Exception ex) {
				System.out.println("Error for aggregateRoot: " + aggregateRoot);
				ex.printStackTrace();
				loopCount++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
