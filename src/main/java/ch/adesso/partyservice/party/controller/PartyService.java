package ch.adesso.partyservice.party.controller;

import java.util.ConcurrentModificationException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import avro.shaded.com.google.common.collect.Lists;
import ch.adesso.partyservice.party.entity.Person;
import ch.adesso.partyservice.party.entity.PersonChangedEvent;
import ch.adesso.partyservice.party.entity.PersonCreatedEvent;

@Stateless
public class PartyService {
	
	@Inject
	private KafkaStore kafkaStore;

	public Person createDummyPerson(String personId) {
		return new Person(personId, "firstname", "lastname", 0L);
	}
	
	public Person getPerson(String personId) {
		return kafkaStore.findById(personId);
	}

	public Person createPerson(Person person) {
		UUID uuid = UUID.randomUUID();
		person.setPartyId(uuid.toString());

		PersonCreatedEvent e = new PersonCreatedEvent(uuid.toString(), 0, person.getFirstname(), person.getLastname());

		kafkaStore.publishEvents(uuid.toString(), Lists.newArrayList(e));

		Person result = kafkaStore.findByIdAndVersion(uuid.toString(), 0);
		
		System.out.println("found person " + person);

		return result;
	}

	public Person updatePerson(String personId, Person person) {
		Person storedPerson = kafkaStore.findById(personId);
		if (storedPerson.getVersion() > person.getVersion()) {
			throw new ConcurrentModificationException("Concurrency Problem");
		}

		long newVersion = person.getVersion() + 1;

		PersonChangedEvent e = new PersonChangedEvent(personId, newVersion, person.getFirstname(),
				person.getLastname());

		kafkaStore.publishEvents(personId, Lists.newArrayList(e));

		return kafkaStore.findByIdAndVersion(personId, newVersion);
	}
}
