package ch.adesso.party.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaProcessorEvent {

	private String aggregateId;
	private long processedVersion;

}
