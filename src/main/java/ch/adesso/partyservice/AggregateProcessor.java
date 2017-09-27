package ch.adesso.partyservice;

import ch.adesso.partyservice.party.passenger.entity.Passenger;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

public class AggregateProcessor implements Processor<String, EventEnvelope> {

    private String partyStoreName;
    private ProcessorContext context;
    private KeyValueStore<String, String> kvPartyStore;

    public AggregateProcessor(String partyStoreName) {
        this.partyStoreName = partyStoreName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        kvPartyStore = (KeyValueStore<String, String>) context.getStateStore(partyStoreName);
    }

    @Override
    public void process(String key, EventEnvelope value) {
        CoreEvent event = value.getEvent();
        AggregateRoot root = null;
        String aggregateAsString = kvPartyStore.get(key);
        if (aggregateAsString == null) {
            root = new Passenger(event.getAggregateId());
        } else {
            JsonReader jsonReader = Json.createReader(new StringReader(aggregateAsString));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            root = new Passenger(jsonObject);
        }

        root.applyEvent(event);

        kvPartyStore.put(key, ((Passenger) root).toJson().toString());

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
