package ch.adesso.teleport.persons.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonCreatedEvent extends PersonEvent {

	public PersonCreatedEvent(String aggregateId) {
		super(PersonCreatedEvent.class, aggregateId, 0);
	}
}
