package ch.adesso.partyservice;

import ch.adesso.partyservice.contact.entity.event.ContactChangedEvent;
import ch.adesso.partyservice.contact.entity.event.ContactCreatedEvent;
import ch.adesso.partyservice.contact.entity.event.ContactDeletedEvent;
import ch.adesso.partyservice.person.entity.event.PassengerCreatedEvent;
import ch.adesso.partyservice.person.entity.event.PersonChangedEvent;
import ch.adesso.partyservice.person.entity.event.PersonCreatedEvent;
import ch.adesso.partyservice.person.entity.event.CredentialsChangedEvent;
import ch.adesso.partyservice.creditcard.entity.event.CreditCardChangedEvent;
import ch.adesso.partyservice.creditcard.entity.event.CreditCardCreatedEvent;
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
		PersonCreatedEvent.class,
		PersonChangedEvent.class,
		CreditCardCreatedEvent.class,
		CreditCardChangedEvent.class,
		ContactCreatedEvent.class,
		ContactChangedEvent.class,
		ContactDeletedEvent.class,
		CredentialsChangedEvent.class})
public class PartyEvent extends CoreEvent {

    public PartyEvent(Class<?> eventType, String aggregateId, long sequence) {
        super(eventType, aggregateId, sequence);
    }
}
