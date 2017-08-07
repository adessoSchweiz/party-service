package ch.adesso.partyservice.party.entity;

import java.util.Collection;
import java.util.Optional;

import org.apache.avro.reflect.Nullable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

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

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = Id.NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Passenger extends PartyRole {

	private String passengerId;

	@Nullable
	private CreditCard creditCard;

	public Passenger(String passengerId) {
		this.passengerId = passengerId;
	}

	public void updatePassenger(Party party, String login, String password, CreditCard creditCard) {

		updateCredentials(login, password);

		if (party != null) {
			Person person = (Person) party;

			// add person data
			updatePersonalData(person.getFirstname(), person.getLastname(), person.getBirthday());

			// add contracts
			Collection<Contact> contacts = person.getContacts();
			if (contacts != null) {
				contacts.forEach(c -> {
					updateContact(c);
				});
			}
		}

		if (creditCard != null) {
			updateCreditCard(creditCard);
		}

	}

	// ----------- events ---------------//

	// ----------- create events ---------------//

	public void applyEvent(PassengerCreatedEvent event) {
		setVersion(event.getSequence());
		setLogin(event.getLogin());
		setPassword(event.getPassword());
	}

	public void applyEvent(PersonCreatedEvent event) {
		setVersion(event.getSequence());

		Person person = new Person(null, event.getFirstname(), event.getLastname(), event.getBirthday());
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
		setCreditCard(creditCard);
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
		person.setBirthday(event.getBirthday());
	}

	public void applyEvent(CreditCardChangedEvent event) {
		setVersion(event.getSequence());
		CreditCard creditCard = getCreditCard();

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

	public void updateCredentials(String login, String password) {
		if (getLogin() == null) {
			PassengerCreatedEvent passengerCreatedEvent = new PassengerCreatedEvent(getPassengerId(), login, password);

			applyChange(passengerCreatedEvent);
		} else if (wasChanged(getLogin(), login) || wasChanged(getPassword(), password)) {

			PassengerChangedEvent passengerChangedEvent = new PassengerChangedEvent(getPassengerId(), login, password);

			applyChange(passengerChangedEvent);

		}
	}

	public void updatePersonalData(String firstname, String lastname, String birthday) {

		if (getParty() == null) {
			PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent(getPassengerId(), firstname, lastname,
					birthday);

			applyChange(personCreatedEvent);
		} else {
			Person person = (Person) getParty();
			if (wasChanged(person.getFirstname(), firstname) || wasChanged(person.getLastname(), lastname)
					|| wasChanged(person.getBirthday(), birthday)) {

				PersonChangedEvent personChangedEvent = new PersonChangedEvent(getPassengerId(), firstname, lastname,
						birthday);

				applyChange(personChangedEvent);
			}
		}

	}

	public void updateCreditCard(CreditCard creditCard) {
		if (getCreditCard() == null) {
			CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getPassengerId(),
					creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
					creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

			applyChange(creditCardCreatedEvent);
		} else {
			CreditCard thisCreditCard = getCreditCard();

			boolean wasChanged = wasChanged(thisCreditCard.getCardNumber(), creditCard.getCardNumber())
					|| wasChanged(thisCreditCard.getCardType(), creditCard.getCardType())
					|| wasChanged(thisCreditCard.getNameOnCard(), creditCard.getNameOnCard())
					|| wasChanged(thisCreditCard.getValidToMonth(), creditCard.getValidToMonth())
					|| wasChanged(thisCreditCard.getValidToYear(), creditCard.getValidToYear())
					|| wasChanged(thisCreditCard.getSecretNumber(), creditCard.getSecretNumber());

			if (wasChanged) {
				CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getPassengerId(),
						creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
						creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

				applyChange(creditCardCreatedEvent);
			}
		}
	}

	public void updateContact(Contact contact) {
		if (getParty() == null) {
			// create data holder, the create person event could came later
			setParty(new Person());
		}

		if (getParty().getContacts() == null || getParty().getContacts().size() == 0) {
			ContactCreatedEvent contactCreatedEvent = new ContactCreatedEvent(getPassengerId(), contact.getAddress(),
					contact.getContactType());

			applyChange(contactCreatedEvent);
		} else {
			Collection<Contact> contacts = getParty().getContacts();

			Optional<Contact> contactOp = contacts.stream().filter(c -> c.getContactId().equals(contact.getContactId()))
					.findFirst();


			if (contactOp.isPresent()) {
				Contact thisContact = contactOp.get();
				boolean wasChanged = wasChanged(thisContact.getContactType(), contact.getContactType())
						|| wasChanged(thisContact.getAddress(), contact.getAddress());


				if (wasChanged) {
					ContactChangedEvent contactChangedEvent = new ContactChangedEvent(getPassengerId(),
							contact.getContactId(), contact.getAddress(), contact.getContactType());

					applyChange(contactChangedEvent);
				}
			}
		}

	}

	// --------- aggregate root ------------ //

	@Override
	public void applyEvent(CoreEvent event) {

		setVersion(event.getSequence());

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

}
