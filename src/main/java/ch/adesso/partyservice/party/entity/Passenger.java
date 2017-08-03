package ch.adesso.partyservice.party.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import avro.shaded.com.google.common.collect.Lists;
import ch.adesso.partyservice.party.command.ContactChangedCommand;
import ch.adesso.partyservice.party.command.ContactCreatedCommand;
import ch.adesso.partyservice.party.command.CreditCardChangedCommand;
import ch.adesso.partyservice.party.command.CreditCardCreatedCommand;
import ch.adesso.partyservice.party.command.PassengerChangedCommand;
import ch.adesso.partyservice.party.command.PassengerCreatedCommand;
import ch.adesso.partyservice.party.command.PersonChangedCommand;
import ch.adesso.partyservice.party.command.PersonCreatedCommand;
import ch.adesso.partyservice.party.event.ContactChangedEvent;
import ch.adesso.partyservice.party.event.ContactCreatedEvent;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.CreditCardChangedEvent;
import ch.adesso.partyservice.party.event.CreditCardCreatedEvent;
import ch.adesso.partyservice.party.event.PassengerChangedEvent;
import ch.adesso.partyservice.party.event.PassengerCreatedEvent;
import ch.adesso.partyservice.party.event.PersonChangedEvent;
import ch.adesso.partyservice.party.event.PersonCreatedEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * AggregateRoot
 * 
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class Passenger extends PartyRole implements AggregateRoot {

	private String passengerId;

	@AvroDefault("0")
	private long version = 0;

	@Nullable
	private CreditCard credidCard;

	@JsonIgnore
	@AvroIgnore
	private Collection<CoreEvent> uncommitedEvents = Lists.newArrayList();

	public Passenger(String passengerId, Party party, String login, String password, CreditCard creditCard) {
		super();

		PassengerCreatedEvent passengerCreatedEvent = new PassengerCreatedEvent(passengerId, login, password);

		applyChange(passengerCreatedEvent);

		if (party != null) {

			version++;
			// add person data
			Person person = (Person) party;
			PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent(passengerId, person.getFirstname(),
					person.getLastname());

			applyChange(personCreatedEvent);

			// add contracts
			Collection<Contact> contacts = person.getContacts();
			if (contacts != null) {
				contacts.forEach(c -> {
					ContactCreatedEvent contactCreatedEvent = new ContactCreatedEvent(passengerId, c.getAddress(),
							c.getContactType());

					applyChange(contactCreatedEvent);
				});
			}
		}

		if (creditCard != null) {
			CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(passengerId,
					creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
					creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

			applyChange(creditCardCreatedEvent);
		}

	}

	// ----------- events ---------------//

	// ----------- create events ---------------//

	public void applyEvent(PassengerCreatedEvent event) {
		setPassengerId(event.getAggregateId());
		setVersion(event.getSequence());
		setLogin(event.getLogin());
		setPassword(event.getPassword());
	}

	public void applyEvent(PersonCreatedEvent event) {
		setPassengerId(event.getAggregateId());
		setVersion(event.getSequence());

		Person person = new Person(null, event.getFirstname(), event.getLastname());
		setParty(person);
	}

	public void applyEvent(ContactCreatedEvent event) {
		setVersion(event.getSequence());
		Contact contact = new Contact(event.getContactId(), event.getAddress(), event.getContactType());
		getParty().addContact(contact);
	}

	public void applyEvent(CreditCardCreatedEvent event) {
		setVersion(event.getSequence());
		CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
				event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());
		setCredidCard(creditCard);
	}

	// ----------- change events -----------//

	public void applyEvent(PassengerChangedEvent event) {
		setVersion(event.getSequence());
		setLogin(event.getLogin());
		setPassword(event.getPassword());
	}

	public void applyEvent(PersonChangedEvent event) {
		setVersion(event.getSequence());
		Person person = (Person) getParty();

		person.setFirstname(event.getFirstname());
		person.setLastname(event.getLastname());
	}

	public void applyEvent(CreditCardChangedEvent event) {
		setVersion(event.getSequence());
		CreditCard creditCard = getCredidCard();

		creditCard.setCardType(event.getCardType());
		creditCard.setCardNumber(event.getCardNumber());
		creditCard.setNameOnCard(event.getNameOnCard());
		creditCard.setSecretNumber(event.getSecretNumber());
		creditCard.setValidToMonth(event.getValidToMonth());
		creditCard.setValidToYear(event.getValidToYear());
	}

	public void applyEvent(ContactChangedEvent event) {
		setVersion(event.getSequence());
		
		Collection<Contact> contacts = getParty().getContacts();

		Contact contact = contacts.stream().filter(c -> c.getContactId().equals(event.getContactId())).findFirst()
				.get();

		contact.setAddress(event.getAddress());
		contact.setContactId(event.getContactId());
		contact.setContactType(event.getContactType());

	}

	// --------- commands ------------//

	// --------- create commands ------------//

	public void applyCommand(PassengerCreatedCommand command) {
		PassengerCreatedEvent passengerCreatedEvent = new PassengerCreatedEvent(command.getAggregateId(),
				 command.getLogin(), command.getPassword());

		applyChange(passengerCreatedEvent);

	}

	public void applyCommand(PersonCreatedCommand command) {
		PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent(command.getAggregateId(),
				 command.getFirstname(), command.getLastname());

		applyChange(personCreatedEvent);

	}

	public void applyCommand(CreditCardCreatedCommand command) {
		CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(command.getAggregateId(),
				 command.getCardNumber(), command.getCardType(), command.getNameOnCard(),
				command.getValidToMonth(), command.getValidToYear(), command.getSecretNumber());

		applyChange(creditCardCreatedEvent);
	}

	public void applyCommand(ContactCreatedCommand command) {
		ContactCreatedEvent contactCreatedEvent = new ContactCreatedEvent(command.getAggregateId(),
				 command.getAddress(), command.getContactType());

		applyChange(contactCreatedEvent);
	}

	// --------- update commands ------------//

	public void applyCommand(PassengerChangedCommand command) {

		if (wasChanged(getLogin(), command.getLogin()) || wasChanged(getPassword(), command.getPassword())) {

			PassengerChangedEvent passengerChangedEvent = new PassengerChangedEvent(command.getAggregateId(),
					command.getLogin(), command.getPassword());

			applyChange(passengerChangedEvent);
		}
	}

	public void applyCommand(PersonChangedCommand command) {

		Person person = (Person) getParty();
		if (wasChanged(person.getFirstname(), command.getFirstname())
				|| wasChanged(person.getLastname(), command.getLastname())) {

			PersonChangedEvent personChangedEvent = new PersonChangedEvent(command.getAggregateId(),
					 command.getFirstname(), command.getLastname());

			applyChange(personChangedEvent);
		}
	}

	public void applyCommand(CreditCardChangedCommand command) {

		CreditCard creditCard = getCredidCard();

		boolean wasChanged = wasChanged(creditCard.getCardNumber(), command.getCardNumber())
				|| wasChanged(creditCard.getCardType(), command.getCardType())
				|| wasChanged(creditCard.getNameOnCard(), command.getNameOnCard())
				|| wasChanged(creditCard.getValidToMonth(), command.getValidToMonth())
				|| wasChanged(creditCard.getValidToYear(), command.getValidToYear())
				|| wasChanged(creditCard.getSecretNumber(), command.getSecretNumber());

		if (wasChanged) {
			CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(command.getAggregateId(),
					 command.getCardNumber(), command.getCardType(),
					command.getNameOnCard(), command.getValidToMonth(), command.getValidToYear(),
					command.getSecretNumber());

			applyChange(creditCardCreatedEvent);
		}
	}

	public void applyCommand(ContactChangedCommand command) {

		Collection<Contact> contacts = getParty().getContacts();

		Optional<Contact> contactOp = contacts.stream().filter(c -> c.getContactId().equals(command.getContactId()))
				.findFirst();

		if (contactOp.isPresent()) {
			Contact contact = contactOp.get();
			boolean wasChanged = wasChanged(contact.getContactType(), command.getContactType())
					|| wasChanged(contact.getAddress(), command.getAddress());

			if (wasChanged) {
				ContactChangedEvent contactChangedEvent = new ContactChangedEvent(command.getAggregateId(),
						command.getContactId(), command.getAddress(),
						command.getContactType());
				;

				applyChange(contactChangedEvent);
			}
		}
	}

	// --------- aggregate root ------------ //

	@Override
	public void applyChange(CoreEvent event) {
		applyEvent(event);
		synchronized (uncommitedEvents) {
			uncommitedEvents.add(event);
		}
	}

	@Override
	public void applyEvent(CoreEvent event) {
		System.out.println("apply CoreEvent: " + event);

		if (event instanceof PassengerCreatedEvent) {
			applyEvent((PassengerCreatedEvent) event);

		} else if ((event instanceof PersonCreatedEvent)) {
			applyEvent((PersonCreatedEvent) event);

		} else if ((event instanceof CreditCardCreatedEvent)) {
			applyEvent((CreditCardCreatedEvent) event);

		} else if ((event instanceof ContactCreatedEvent)) {
			applyEvent((ContactCreatedEvent) event);

		} else if ((event instanceof PassengerChangedEvent)) {
			applyEvent((PassengerChangedEvent) event);

		} else if ((event instanceof PersonChangedEvent)) {
			applyEvent((PersonChangedEvent) event);

		} else if ((event instanceof CreditCardChangedEvent)) {
			applyEvent((CreditCardChangedEvent) event);

		} else if ((event instanceof ContactChangedEvent)) {
			applyEvent((ContactChangedEvent) event);
		}
	}

	@Override
	public Collection<CoreEvent> getUncommitedEvents() {
		return Collections.unmodifiableCollection(uncommitedEvents);
	}

	private boolean wasChanged(Object o1, Object o2) {
		return o1 == null ? o2 != null : !o1.equals(o2);
	}

}
