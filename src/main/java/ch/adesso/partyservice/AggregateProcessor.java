package ch.adesso.partyservice;

import ch.adesso.partyservice.party.PartyEvent;
import ch.adesso.partyservice.party.PartyEventStream;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

public class AggregateProcessor implements Processor<String, EventEnvelope> {

    private String partyStoreName;
    private ProcessorContext context;
    private KeyValueStore<String, PartyEventStream> kvPartyStore;

    public AggregateProcessor(String partyStoreName) {
        this.partyStoreName = partyStoreName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        kvPartyStore = (KeyValueStore<String, PartyEventStream>) context.getStateStore(partyStoreName);
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

        context.commit();
    }

    @Override
    public void punctuate(long timestamp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        kvPartyStore.close();
    }

}
