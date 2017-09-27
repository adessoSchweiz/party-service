package ch.adesso.partyservice.party;

import ch.adesso.partyservice.CoreEvent;
import ch.adesso.partyservice.party.passenger.CredentialsChangedEvent;
import ch.adesso.partyservice.party.passenger.PersonChangedEvent;
import ch.adesso.partyservice.party.passenger.PersonCreatedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.CreditCardChangedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.CreditCardCreatedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.PassengerCreatedEvent;
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
        PersonCreatedEvent.class,
        PersonChangedEvent.class,
        CreditCardCreatedEvent.class,
        CreditCardChangedEvent.class,
        CredentialsChangedEvent.class})
public class PartyEvent extends CoreEvent {

    public PartyEvent(Class<?> eventType, String aggregateId, long sequence) {
        super(eventType, aggregateId, sequence);
    }
}
