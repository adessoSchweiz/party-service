package ch.adesso.partyservice.party;

import ch.adesso.partyservice.CoreEvent;
import ch.adesso.partyservice.party.passenger.entity.event.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Union;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@Union({PassengerCreatedEvent.class,
        PassengerChangedEvent.class,
        AddressCreatedEvent.class,
        CreditCardCreatedEvent.class,
        CreditCardChangedEvent.class})
public class PartyEvent extends CoreEvent {

    public PartyEvent(Class<?> eventType, String aggregateId, long sequence) {
        super(eventType, aggregateId, sequence);
    }
}
