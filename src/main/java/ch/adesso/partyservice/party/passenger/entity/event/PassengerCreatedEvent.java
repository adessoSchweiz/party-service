package ch.adesso.partyservice.party.passenger.entity.event;

import ch.adesso.partyservice.party.PartyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerCreatedEvent extends PartyEvent {

    public PassengerCreatedEvent(String aggregateId) {
        super(PassengerCreatedEvent.class, aggregateId, 0);
    }
}
