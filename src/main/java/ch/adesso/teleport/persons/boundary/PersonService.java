package ch.adesso.teleport.persons.boundary;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.adesso.teleport.kafka.config.Topics;
import ch.adesso.teleport.persons.controller.PersonEventPublisherProvider;
import ch.adesso.teleport.persons.controller.PersonLocalStoreProvider;
import ch.adesso.teleport.persons.entity.Person;

@Stateless
public class PersonService {

	@Inject
	private PersonEventPublisherProvider personEventPublisherProvider;

	@Inject
	private PersonLocalStoreProvider personsLocalStoreProvider;

	public Person createPerson(Person person) {
		String personId = UUID.randomUUID().toString();
		Person newPerson = new Person(personId);
		newPerson.updateFrom(person);

		personEventPublisherProvider.getEventPublisher().save(Topics.PERSON_EVENT_TOPIC.toString(), newPerson);
		return newPerson;
	}

	public Person updatePerson(Person person) {
		Person storedPerson = personsLocalStoreProvider.getKafkaLocalStore()
				.findByIdAndVersion(Topics.PERSON_AGGREGATE_STORE.toString(), person);

		storedPerson.updateFrom(person);
		personEventPublisherProvider.getEventPublisher().save(Topics.PERSON_EVENT_TOPIC.toString(), storedPerson);
		return storedPerson;
	}

	public Person findPersonById(String personId) {
		return personsLocalStoreProvider.getKafkaLocalStore().findById(Topics.PERSON_AGGREGATE_STORE.toString(),
				personId);
	}
}