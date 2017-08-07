package ch.adesso.partyservice.party.controller;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import ch.adesso.partyservice.party.entity.AggregateRootId;
import ch.adesso.partyservice.party.entity.Contact;
import ch.adesso.partyservice.party.entity.ContactTypeEnum;
import ch.adesso.partyservice.party.entity.CreditCard;
import ch.adesso.partyservice.party.entity.CreditCardTypeEnum;
import ch.adesso.partyservice.party.entity.Party;
import ch.adesso.partyservice.party.entity.Passenger;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.entity.PostalAddress;

@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PassengerService {

	@Inject
	private KafkaStore kafkaStore;

	public Passenger createDummyPassenger(String passengerId) {
		Person person = new Person(null, "firstname", "lastname", "1990-10-10");
		person.addContact(new Contact(UUID.randomUUID().toString(),
				new PostalAddress(UUID.randomUUID().toString(), "street", "12A", "Zürich", "8888", "Schweiz"),
				ContactTypeEnum.DOMICILE));

		CreditCard cCard = new CreditCard("123333", CreditCardTypeEnum.VISA, "owner", 2, 2020, 121);
		Passenger passenger = new Passenger(UUID.randomUUID().toString());
		passenger.updatePassenger(person, "login", "password", cCard);

		return passenger;
	}

	public Passenger getPassengerWithVersion(String passengerId, Long version) {
		Passenger passenger = null;
		if (version == null) {
			passenger = getPassenger(passengerId);
		} else {
			passenger = kafkaStore.findByIdAndVersion(passengerId, version);
		}

		if (passenger == null) {
			throw new EntityNotFoundException("Passenger for Id: " + passengerId + " not found.");
		}

		return passenger;
	}

	public Passenger getPassengerByLogin(String login, String password) {
		Passenger passenger = null;

		passenger = kafkaStore.findByCredentials(login, password);

		if (passenger == null) {
			throw new EntityNotFoundException("Passenger for login: " + login + " not found.");
		}

		return passenger;
	}

	public Passenger getPassenger(String passengerId) {
		Passenger passenger = kafkaStore.findById(passengerId);
		if (passenger == null) {
			throw new EntityNotFoundException("Passenger for Id: " + passengerId + " not found.");
		}

		return passenger;
	}

	public Passenger createAndReturnPassenger(Passenger passenger) {
		AggregateRootId rootId = createPassenger(passenger);
		return kafkaStore.findByIdAndVersion(rootId.getId(), rootId.getVersion());
	}

	public AggregateRootId createPassenger(Passenger passenger) {

		String passengerId = UUID.randomUUID().toString();
		Passenger newPassenger = new Passenger(passengerId);
		newPassenger.updatePassenger(passenger.getParty(), passenger.getLogin(), passenger.getPassword(),
				passenger.getCreditCard());

		long version = kafkaStore.publishEvents(passengerId, 0, newPassenger.getUncommitedEvents());
		return new AggregateRootId(passengerId, version);
	}

	public Passenger updateAndReturnPassenger(String passengerId, Passenger passenger) {
		AggregateRootId rootId = updatePassenger(passengerId, passenger);
		return kafkaStore.findByIdAndVersion(rootId.getId(), rootId.getVersion());
	}

	public AggregateRootId updatePassenger(String passengerId, Passenger passenger) {

		Passenger storedPassenger = kafkaStore.findByIdAndVersion(passengerId, passenger.getVersion());

		if (storedPassenger.getPassengerId().equals(passenger.getPassengerId())
				&& storedPassenger.getVersion() > passenger.getVersion()) {
			System.out.println("storedPassenger:" + storedPassenger + " input: " + passenger);
			throw new ConcurrentModificationException("Concurrency Problem");
		}

		long newVersion = passenger.getVersion() + 1;

		storedPassenger.updateCredentials(passenger.getLogin(), passenger.getPassword());

		// if provided add credit card
		CreditCard creditCard = passenger.getCreditCard();
		if (creditCard != null) {
			storedPassenger.updateCreditCard(creditCard);
		}

		// add person data
		Party party = passenger.getParty();

		if (party != null) {
			Person person = (Person) party;

			storedPassenger.updatePersonalData(person.getFirstname(), person.getLastname(), person.getBirthday());

			// add contracts
			List<Contact> contacts = person.getContacts();
			if (contacts != null) {
				contacts.forEach(c -> {
					storedPassenger.updateContact(c);
				});
			}
		}

		long version = kafkaStore.publishEvents(passengerId, newVersion, storedPassenger.getUncommitedEvents());
		return new AggregateRootId(passengerId, version);
	}

}
