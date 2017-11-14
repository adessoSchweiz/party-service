package ch.adesso.teleport.kafka.store;

import ch.adesso.teleport.CoreEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessedEvent {
	private CoreEvent event;
}
