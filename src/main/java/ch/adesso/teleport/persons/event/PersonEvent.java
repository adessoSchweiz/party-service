package ch.adesso.teleport.persons.event;

import org.apache.avro.reflect.Union;

import ch.adesso.teleport.CoreEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Union({ PersonCreatedEvent.class, PersonChangedEvent.class, PersonContactChangedEvent.class,
		PersonStatusChangedEvent.class })
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class PersonEvent extends CoreEvent {

	public PersonEvent(Class<?> eventType, String aggregateId, long sequence) {
		super(eventType.getSimpleName(), aggregateId, sequence);
	}
}