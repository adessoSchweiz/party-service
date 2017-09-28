package ch.adesso.partyservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessorEvent {

	private String aggregateId;
	private long processedVersion;

}
