package ch.adesso.partyservice.party;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import ch.adesso.partyservice.EventEnvelope;
import ch.adesso.partyservice.Header;

public class PartyTransformer implements Transformer<String, EventEnvelope, KeyValue<String, EventEnvelope>> {

	private ProcessorContext context;

	@Override
	public void init(ProcessorContext context) {
		this.context = context;
	}

	@Override
	public KeyValue<String, EventEnvelope> transform(String key, EventEnvelope value) {
		Header h = new Header();
		h.addProperty("offset", context.offset());
		value.setHeader(h);
		return KeyValue.pair(key, value);
	}

	@Override
	public KeyValue<String, EventEnvelope> punctuate(long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
