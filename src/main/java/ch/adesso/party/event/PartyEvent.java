package ch.adesso.party.event;

import org.apache.avro.reflect.Union;

import ch.adesso.party.CoreEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
