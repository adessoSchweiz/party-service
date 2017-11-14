package ch.adesso.teleport;

public interface EventEnvelope<T extends CoreEvent> {

	// this method should be annotated with Avro @Union({}) in sub classes
	public T getEvent();
}
