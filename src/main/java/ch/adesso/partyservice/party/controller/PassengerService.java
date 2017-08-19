package ch.adesso.partyservice.party.controller;

import java.util.ConcurrentModificationException;
import java.util.UUID;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import ch.adesso.partyservice.party.entity.Contact;
import ch.adesso.partyservice.party.entity.ContactTypeEnum;
import ch.adesso.partyservice.party.entity.CreditCard;
import ch.adesso.partyservice.party.entity.CreditCardTypeEnum;
import ch.adesso.partyservice.party.entity.Passenger;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.entity.PostalAddress;

@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PassengerService {

	@Inject
	private KafkaStore kafkaStore;

	public Person createDummyPassenger(String personId) {
		Person person = new Person(personId);
		person.updatePersonalData("firstname", "lastname", "12-12-2000");
		person.updateCredentials("login", "password");
		Passenger passenger = new Passenger(null, person);
		person.updatePartyRole(passenger);

		Contact contact = new Contact(null,
				new PostalAddress(UUID.randomUUID().toString(), "street", "12A", "Zürich", "8888", "Schweiz"),
				ContactTypeEnum.DOMICILE);

		person.updateContact(contact);

		CreditCard cCard = new CreditCard("123333", CreditCardTypeEnum.VISA, "owner", 2, 2020, 121);
		person.updateCreditCard(cCard);

		return person;
	}

	public Person getPersonWithVersion(String personId, Long version) {
		Person person = null;
		if (version == null) {
			person = getPerson(personId);
		} else {
			person = kafkaStore.findByIdAndVersion(personId, version, Person.class);
		}

		if (person == null) {
			throw new EntityNotFoundException("Person for Id: " + personId + " not found.");
		}

		return person;
	}

	public Person getPersonByLogin(String login, String password) {
		Person person = null;

		person = kafkaStore.findByCredentials(login, password);

		if (person == null) {
			throw new EntityNotFoundException("Passenger for login: " + login + " not found.");
		}

		return person;
	}

	public Person getPerson(String personId) {
		Person person = kafkaStore.findById(personId, Person.class);
		if (person == null) {
			throw new EntityNotFoundException("Person for Id: " + personId + " not found.");
		}

		return person;
	}

	public Person createPerson(Person person) {

		String personId = UUID.randomUUID().toString();
		Person newPerson = new Person(personId);
		newPerson.updatePersonalData(person.getFirstname(), person.getLastname(), person.getBirthday());
		newPerson.updateCredentials(person.getLogin(), person.getPassword());

		// roles
		if (person.getPartyRoles() != null) {
			person.getPartyRoles().forEach(newPerson::updatePartyRole);
		}

		// contacts
		if (person.getContacts() != null) {
			person.getContacts().forEach(newPerson::updateContact);
		}

		kafkaStore.publishEvents(newPerson.getUncommitedEvents());
		newPerson.clearEvents();

		return newPerson;
	}

	public Person updatePerson(Person person) {

		Person storedPerson = kafkaStore.findByIdAndVersion(person.getId(), person.getVersion(), Person.class);

		if (storedPerson.getId().equals(person.getId()) && storedPerson.getVersion() > person.getVersion()) {
			System.out.println("storedPerson:" + storedPerson + " input: " + person);
			throw new ConcurrentModificationException("Concurrency Problem");
		}

		storedPerson.updateCredentials(person.getLogin(), person.getPassword());
		storedPerson.updatePersonalData(person.getFirstname(), person.getLastname(), person.getBirthday());

		// roles
		if (person.getPartyRoles() != null) {
			person.getPartyRoles().forEach(storedPerson::updatePartyRole);
		}

		// contacts
		if (person.getContacts() != null) {
			person.getContacts().forEach(storedPerson::updateContact);
		}

		kafkaStore.publishEvents(storedPerson.getUncommitedEvents());
		storedPerson.clearEvents();

		return storedPerson;
	}
}