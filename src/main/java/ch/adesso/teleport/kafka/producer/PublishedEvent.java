package ch.adesso.teleport.kafka.producer;

import ch.adesso.teleport.CoreEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PublishedEvent {
	private CoreEvent event;
}
