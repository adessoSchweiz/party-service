package ch.adesso.teleport.kafka.serializer;

import ch.adesso.teleport.EventEnvelope;

public class EventEnvelopeAvroDeserializer extends KafkaAvroReflectDeserializer<EventEnvelope> {
	public EventEnvelopeAvroDeserializer() {
		super(EventEnvelope.class);
	}
}
