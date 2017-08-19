package ch.adesso.partyservice.party.kafka;

import java.util.function.Consumer;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.entity.PartyEventStream;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.CredentialsChangedEvent;
import ch.adesso.partyservice.party.event.EventEnvelope;
import ch.adesso.partyservice.party.event.PartyEvent;

public class AggregateProcessor implements Processor<String, EventEnvelope> {

	private String partyStoreName;
	private String partyLoginStoreName;
	private ProcessorContext context;
	private Consumer<CoreEvent> eventConsumer;
	private KeyValueStore<String, PartyEventStream> kvPartyStore;
	private KeyValueStore<String, PartyEventStream> kvLoginStore;

	public AggregateProcessor(String partyStoreName, String partyLoginStoreName, Consumer<CoreEvent> eventConsumer) {
		this.partyStoreName = partyStoreName;
		this.partyLoginStoreName = partyLoginStoreName;
		this.eventConsumer = eventConsumer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		kvPartyStore = (KeyValueStore<String, PartyEventStream>) context.getStateStore(partyStoreName);
		kvLoginStore = (KeyValueStore<String, PartyEventStream>) context.getStateStore(partyLoginStoreName);
	}

	@Override
	public void process(String key, EventEnvelope value) {
		PartyEvent event = value.getEvent();
		PartyEventStream stream = kvPartyStore.get(key);
		if (stream == null) {
			stream = new PartyEventStream(event.getAggregateId());
		}

		stream.setAggregateVersion(event.getSequence());
		stream.getLastEvents().put(event.getEventType(), event);

		kvPartyStore.put(key, stream);

		String login = null;

		if (event instanceof CredentialsChangedEvent) {
			login = ((CredentialsChangedEvent) event).getLogin();
		} else {
			PartyEvent credentials = stream.getLastEvents().get(CredentialsChangedEvent.class.getName());
			if (credentials != null) {
				login = ((CredentialsChangedEvent) credentials).getLogin();
			}
		}

		if (login != null) {
			kvLoginStore.put(login, stream);
		}

		context.commit();

		eventConsumer.accept(event);
	}

	@Override
	public void punctuate(long timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		kvPartyStore.close();
		kvLoginStore.close();
	}

}
