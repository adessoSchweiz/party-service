package ch.adesso.teleport;

import org.apache.avro.reflect.AvroName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CoreEvent {

	@AvroName("event_type")
	private String eventType;
	@AvroName("aggregate_id")
	private String aggregateId;
	private long sequence;
	private long timestamp;

	public CoreEvent(String eventType, String aggregateId, long sequence) {
		this.timestamp = System.nanoTime();
		this.eventType = eventType;
		this.aggregateId = aggregateId;
		this.sequence = sequence;
	}

}