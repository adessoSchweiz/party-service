package ch.adesso.partyservice.party.person.passenger.entity.event;

import ch.adesso.partyservice.PartyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerChangedEvent extends PartyEvent {

	private String passengerId;

	public PassengerChangedEvent(String aggregateId, long sequence, String passengerId) {
		super(PassengerChangedEvent.class, aggregateId, sequence);
		this.passengerId = passengerId;
	}
}
