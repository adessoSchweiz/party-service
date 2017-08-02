package ch.adesso.partyservice.party.kafka;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.entity.EventEnvelope;
import ch.adesso.partyservice.party.entity.Person;

public class PartyProcessor implements Processor<String, EventEnvelope> {

	private String storeName;
	private ProcessorContext context;
	private KeyValueStore<String, Person> kvStore;

	public PartyProcessor(String storeName) {
		this.storeName = storeName;
	}

	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvStore = (KeyValueStore) context.getStateStore(storeName);
	}

	@Override
	public void process(String key, EventEnvelope value) {
		Person person = kvStore.get(key);
		if (person == null) {
			person = new Person();
		}
		person.applyEvent(value.getEvent());
		kvStore.put(key, person);

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
