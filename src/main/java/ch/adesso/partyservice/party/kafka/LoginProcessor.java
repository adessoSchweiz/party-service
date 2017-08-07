package ch.adesso.partyservice.party.kafka;

import java.util.function.Supplier;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.entity.AggregateRoot;
import ch.adesso.partyservice.party.entity.AggregateRootFactory;
import ch.adesso.partyservice.party.entity.Passenger;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.EventEnvelope;

public class LoginProcessor<T extends AggregateRoot> implements Processor<String, EventEnvelope> {

	private String passengerStoreName;
	private String passengerLoginStoreName;
	Supplier<AggregateRootFactory<T>> aggregateRootFactoryProvider;
	private ProcessorContext context;
	private KeyValueStore<String, T> kvPassengerStore;
	private KeyValueStore<String, T> kvLoginStore;

	public LoginProcessor(String passengerStoreName, String passengerLoginStoreName,
			Supplier<AggregateRootFactory<T>> aggregateRootFactoryProvider) {
		this.passengerStoreName = passengerStoreName;
		this.passengerLoginStoreName = passengerLoginStoreName;
		this.aggregateRootFactoryProvider = aggregateRootFactoryProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvPassengerStore = (KeyValueStore<String, T>) context.getStateStore(passengerStoreName);
		kvLoginStore = (KeyValueStore<String, T>) context.getStateStore(passengerLoginStoreName);
	}

	@Override
	public void process(String key, EventEnvelope value) {
		CoreEvent event = value.getEvent();
		T aggregateRoot = kvPassengerStore.get(key);
		if (aggregateRoot == null) {
			aggregateRoot = aggregateRootFactoryProvider.get().newInstance(event.getAggregateId());
		}

		@SuppressWarnings("unused")
		Passenger passenger = (Passenger) aggregateRoot;

		kvLoginStore.put(passenger.getLogin() + passenger.getPassword(), aggregateRoot);
		kvLoginStore.flush();

		context.commit();

	}

	@Override
	public void punctuate(long timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		kvPassengerStore.close();
		kvLoginStore.close();
	}

}
