package ch.adesso.partyservice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CoreEvent {
	private String aggregateId;
	private String eventType;
	private long timestamp;
	private long sequence;

	public CoreEvent(Class<?> eventType, String aggregateId, long sequence) {
		this.timestamp = System.nanoTime();
		this.eventType = eventType.getName();
		this.aggregateId = aggregateId;
		this.sequence = sequence;
	}
}