package ch.adesso.partyservice.party.person;

import ch.adesso.partyservice.party.PartyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonCreatedEvent extends PartyEvent {

	public PersonCreatedEvent(String aggregateId, long sequence) {
		super(PersonCreatedEvent.class, aggregateId, sequence);
	}
}
