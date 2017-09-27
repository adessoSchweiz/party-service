package ch.adesso.partyservice;

import ch.adesso.partyservice.party.PartyEvent;
import ch.adesso.partyservice.party.PartyEventStream;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import ch.adesso.partyservice.party.passenger.CredentialsChangedEvent;

public class AggregateProcessor implements Processor<String, EventEnvelope> {

	private String partyStoreName;
	private String partyLoginStoreName;
	private ProcessorContext context;
	private KeyValueStore<String, PartyEventStream> kvPartyStore;
	private KeyValueStore<String, PartyEventStream> kvLoginStore;

	public AggregateProcessor(String partyStoreName, String partyLoginStoreName) {
		this.partyStoreName = partyStoreName;
		this.partyLoginStoreName = partyLoginStoreName;
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
