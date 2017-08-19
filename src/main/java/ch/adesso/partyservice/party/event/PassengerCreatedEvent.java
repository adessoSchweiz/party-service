package ch.adesso.partyservice.party.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerCreatedEvent extends PartyEvent {

	public PassengerCreatedEvent(String aggregateId, long sequence) {
		super(PassengerCreatedEvent.class, aggregateId, sequence);
	}
}
