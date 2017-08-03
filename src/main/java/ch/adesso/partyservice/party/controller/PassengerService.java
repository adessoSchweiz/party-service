package ch.adesso.partyservice.party.controller;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.adesso.partyservice.party.command.ContactChangedCommand;
import ch.adesso.partyservice.party.command.CreditCardChangedCommand;
import ch.adesso.partyservice.party.command.PassengerChangedCommand;
import ch.adesso.partyservice.party.command.PersonChangedCommand;
import ch.adesso.partyservice.party.entity.Contact;
import ch.adesso.partyservice.party.entity.ContactTypeEnum;
import ch.adesso.partyservice.party.entity.CreditCard;
import ch.adesso.partyservice.party.entity.CreditCardTypeEnum;
import ch.adesso.partyservice.party.entity.Party;
import ch.adesso.partyservice.party.entity.Passenger;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.entity.PostalAddress;

@Stateless
public class PassengerService {

	@Inject
	private KafkaStore kafkaStore;

	public Passenger createDummyPassenger(String passengerId) {
		Person person = new Person(null, "firstname", "lastname", "1990-10-10");
		person.addContact(new Contact(UUID.randomUUID().toString(),
				new PostalAddress(UUID.randomUUID().toString(), "street", "12A", "Zürich", "8888", "Schweiz"),
				ContactTypeEnum.DOMICILE));

		CreditCard cCard = new CreditCard("123333", CreditCardTypeEnum.VISA, "owner", 2,
				2020, 121);
		Passenger passenger = new Passenger(UUID.randomUUID().toString(), person, "login", "password", cCard);

		return passenger;
	}

	public Passenger getPassenger(String passengerId) {
		return kafkaStore.findById(passengerId);
	}

	public Passenger createPassenger(Passenger passenger) {
		String passengerId = UUID.randomUUID().toString();

		Passenger newPassenger = new Passenger(passengerId, passenger.getParty(), passenger.getLogin(),
				passenger.getPassword(), passenger.getCreditCard());

		long seq = kafkaStore.publishEvents(passengerId, 0, newPassenger.getUncommitedEvents());

		Passenger result = kafkaStore.findByIdAndVersion(passengerId, seq);

		System.out.println("found passenger: " + passenger);
		
		return result;
	}

	public Passenger updatePassenger(String passengerId, Passenger passenger) {

		Passenger storedPassenger = kafkaStore.findById(passengerId);
		if (storedPassenger.getVersion() > passenger.getVersion()) {
			throw new ConcurrentModificationException("Concurrency Problem");
		}

		long newVersion = passenger.getVersion() + 1;

		PassengerChangedCommand passengerChangedCommand = new PassengerChangedCommand(passengerId, newVersion,
				passenger.getLogin(), passenger.getPassword());

		storedPassenger.applyCommand(passengerChangedCommand);

		// if provided add credit card
		CreditCard creditCard = passenger.getCreditCard();
		if (creditCard != null) {
			CreditCardChangedCommand creditCardChangedCommand = new CreditCardChangedCommand(passengerId, newVersion,
					creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
					creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

			storedPassenger.applyCommand(creditCardChangedCommand);
		}

		// add person data
		Party party = passenger.getParty();

		if (party != null) {
			Person person = (Person) party;
			PersonChangedCommand personChangedCommand = new PersonChangedCommand(passengerId, newVersion,
					person.getFirstname(), person.getLastname(), person.getBirthday());

			storedPassenger.applyCommand(personChangedCommand);

			// add contracts
			List<Contact> contacts = person.getContacts();
			if (contacts != null) {
				contacts.forEach(c -> {
					ContactChangedCommand contactChangedEvent = new ContactChangedCommand(passengerId, newVersion, c.getContactId(),
							c.getAddress(), c.getContactType());
					storedPassenger.applyCommand(contactChangedEvent);
				});
			}
		}


		long seq  = kafkaStore.publishEvents(passengerId, newVersion, storedPassenger.getUncommitedEvents());

		return kafkaStore.findByIdAndVersion(passengerId, seq);
	}
}
