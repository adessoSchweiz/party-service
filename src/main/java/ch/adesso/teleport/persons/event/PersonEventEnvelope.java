package ch.adesso.teleport.persons.event;

import ch.adesso.teleport.EventEnvelope;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
@Data
public class PersonEventEnvelope implements EventEnvelope<PersonEvent> {

	@Getter
	private PersonEvent event;

	public PersonEventEnvelope(PersonEvent event) {
		this.event = event;
	}

}
