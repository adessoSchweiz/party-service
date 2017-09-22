package ch.adesso.partyservice.party.entity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.apache.avro.reflect.Nullable;

import ch.adesso.partyservice.party.event.ContactChangedEvent;
import ch.adesso.partyservice.party.event.ContactCreatedEvent;
import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.CredentialsChangedEvent;
import ch.adesso.partyservice.party.event.CreditCardChangedEvent;
import ch.adesso.partyservice.party.event.CreditCardCreatedEvent;
import ch.adesso.partyservice.party.event.PassengerChangedEvent;
import ch.adesso.partyservice.party.event.PassengerCreatedEvent;
import ch.adesso.partyservice.party.event.PersonChangedEvent;
import ch.adesso.partyservice.party.event.PersonCreatedEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person extends Party {

	@Nullable
	private String login;

	@Nullable
	private String password;

	@Nullable
	private String firstname;

	@Nullable
	private String lastname;

	@Nullable
	private String birthday;

	public Person() {
		super();
	}

	public Person(String id) {
		super();
		applyChange(new PersonCreatedEvent(id, getVersion()));
	}

	// ----------- events ---------------//

	// ----------- create events ---------------//
	public void applyEvent(PersonCreatedEvent event) {
		setId(event.getAggregateId());
		setVersion(event.getSequence());
	}

	public void applyEvent(PassengerCreatedEvent event) {
		setVersion(event.getSequence());
		Passenger passenger = new Passenger(UUID.randomUUID().toString(), this);
		addPartyRole(passenger);

	}

	public void applyEvent(ContactCreatedEvent event) {
		setVersion(event.getSequence());
		Contact contact = new Contact(UUID.randomUUID().toString(), event.getAddress(), event.getContactType());
		addContact(contact);
	}

	public void applyEvent(CreditCardCreatedEvent event) {
		setVersion(event.getSequence());
		CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
				event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

		Passenger passenger = getPartyRole(Passenger.class);
		passenger.setCreditCard(creditCard);
	}

	// ----------- change events -----------//

	public void applyEvent(PersonChangedEvent event) {
		setVersion(event.getSequence());

		setFirstname(event.getFirstname());
		setLastname(event.getLastname());
		setBirthday(event.getBirthday());
	}

	public void applyEvent(CredentialsChangedEvent event) {
		setVersion(event.getSequence());

		setLogin(event.getLogin());
		setPassword(event.getPassword());
	}

	public void applyEvent(CreditCardChangedEvent event) {
		setVersion(event.getSequence());
		Passenger passenger = getPartyRole(Passenger.class);
		CreditCard creditCard = passenger.getCreditCard();

		creditCard.setCardType(event.getCardType());
		creditCard.setCardNumber(event.getCardNumber());
		creditCard.setNameOnCard(event.getNameOnCard());
		creditCard.setSecretNumber(event.getSecretNumber());
		creditCard.setValidToMonth(event.getValidToMonth());
		creditCard.setValidToYear(event.getValidToYear());
	}

	public void applyEvent(ContactChangedEvent event) {
		setVersion(event.getSequence());

		Collection<Contact> contacts = getContacts();

		Contact contact = contacts.stream().filter(c -> c.getContactId().equals(event.getContactId())).findFirst()
				.get();

		contact.setAddress(event.getAddress());
		contact.setContactId(event.getContactId());
		contact.setContactType(event.getContactType());

	}

	public void applyEvent(PassengerChangedEvent event) {
		setVersion(event.getSequence());

		Collection<PartyRole> roles = getPartyRoles();

		PartyRole role = roles.stream().filter(r -> r.getId().equals(event.getPassengerId())).findFirst().get();

		// TODO
	}

	// --------- commands ------------//

	// --------- create commands ------------//

	public void updateCredentials(String login, String password) {
		applyChange(new CredentialsChangedEvent(getId(), getNextVersion(), login, password));
	}

	public void updatePersonalData(String firstname, String lastname, String birthday) {
		if (wasChanged(getFirstname(), firstname) || wasChanged(getLastname(), lastname)
				|| wasChanged(getBirthday(), birthday)) {

			PersonChangedEvent personChangedEvent = new PersonChangedEvent(getId(), getNextVersion(), firstname,
					lastname, birthday);

			applyChange(personChangedEvent);
		}
	}

	public void updateCreditCard(CreditCard creditCard) {
		if (creditCard == null) {
			return;
		}

		Passenger passenger = getPartyRole(Passenger.class);
		if (passenger.getCreditCard() == null) {
			CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getId(), getNextVersion(),
					creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
					creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

			applyChange(creditCardCreatedEvent);
		} else {
			CreditCard thisCreditCard = passenger.getCreditCard();

			boolean wasChanged = wasChanged(thisCreditCard.getCardNumber(), creditCard.getCardNumber())
					|| wasChanged(thisCreditCard.getCardType(), creditCard.getCardType())
					|| wasChanged(thisCreditCard.getNameOnCard(), creditCard.getNameOnCard())
					|| wasChanged(thisCreditCard.getValidToMonth(), creditCard.getValidToMonth())
					|| wasChanged(thisCreditCard.getValidToYear(), creditCard.getValidToYear())
					|| wasChanged(thisCreditCard.getSecretNumber(), creditCard.getSecretNumber());

			if (wasChanged) {
				CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getId(), getNextVersion(),
						creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
						creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

				applyChange(creditCardCreatedEvent);
			}
		}
	}

	public void updateContact(Contact contact) {

		if (contact == null) {
			return;
		}

		if (contact.getContactId() == null || contact.getContactId().trim().equals("")) {
			ContactCreatedEvent contactCreatedEvent = new ContactCreatedEvent(getId(), getNextVersion(),
					contact.getAddress(), contact.getContactType());

			applyChange(contactCreatedEvent);
		} else {
			Collection<Contact> contacts = getContacts();

			Optional<Contact> contactOp = contacts.stream().filter(c -> c.getContactId().equals(contact.getContactId()))
					.findFirst();

			if (contactOp.isPresent()) {
				Contact thisContact = contactOp.get();
				boolean wasChanged = wasChanged(thisContact.getContactType(), contact.getContactType())
						|| wasChanged(thisContact.getAddress(), contact.getAddress());

				if (wasChanged) {
					ContactChangedEvent contactChangedEvent = new ContactChangedEvent(getId(), getNextVersion(),
							contact.getContactId(), contact.getAddress(), contact.getContactType());

					applyChange(contactChangedEvent);
				}
			}
		}

	}

	public void updatePartyRole(PartyRole partyRole) {

		if (partyRole == null) {
			return;
		}

		if (partyRole.getId() == null || partyRole.getId().trim().equals("")) {
			if (partyRole instanceof Passenger) {
				applyChange(new PassengerCreatedEvent(getId(), getNextVersion()));
				Passenger passenger = (Passenger) partyRole;
				updateCreditCard(passenger.getCreditCard());
			}
		} else {
			Collection<PartyRole> partyRoles = getPartyRoles();

			Optional<PartyRole> roleOp = partyRoles.stream().filter(r -> r.getId().equals(partyRole.getId()))
					.findFirst();

			if (roleOp.isPresent()) {
				PartyRole thisRole = roleOp.get();
				applyChange(new PassengerChangedEvent(getId(), getNextVersion(), thisRole.getId()));

				if (partyRole instanceof Passenger) {
					Passenger passenger = (Passenger) partyRole;
					updateCreditCard(passenger.getCreditCard());
				}
			}
		}
	}

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

		} else if ((event instanceof PersonChangedEvent)) {
			applyEvent((PersonChangedEvent) event);

		} else if ((event instanceof CredentialsChangedEvent)) {
			applyEvent((CredentialsChangedEvent) event);

		} else if ((event instanceof CreditCardChangedEvent)) {
			applyEvent((CreditCardChangedEvent) event);

		} else if ((event instanceof ContactChangedEvent)) {
			applyEvent((ContactChangedEvent) event);
		}
	}
}
