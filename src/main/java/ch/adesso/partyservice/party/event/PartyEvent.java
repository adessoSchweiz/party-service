package ch.adesso.partyservice.party.event;

import org.apache.avro.reflect.Union;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString(callSuper=true)
@Union({PassengerCreatedEvent.class,
		PassengerChangedEvent.class,
		PersonCreatedEvent.class, 
		PersonChangedEvent.class,
		CreditCardCreatedEvent.class,
		CreditCardChangedEvent.class,
		ContactCreatedEvent.class,
		ContactChangedEvent.class,
		ContactDeletedEvent.class})
public class PartyEvent extends CoreEvent {

    public PartyEvent(Class<?> eventType, String aggregateId) {
        super(eventType, aggregateId);
    }
}
