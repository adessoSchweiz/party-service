package ch.adesso.partyservice.person.boundary;

import ch.adesso.partyservice.party.controller.KafkaStore;
import ch.adesso.partyservice.person.entity.Person;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.UUID;

@Stateless
public class PersonService {

    @Inject
    private KafkaStore kafkaStore;

    public Person createPerson(Person person) {
        String personId = UUID.randomUUID().toString();
        Person newPerson = new Person(personId);
        updatePerson(person, newPerson);
        return kafkaStore.findByIdAndVersionWaitForResul(newPerson.getId(), newPerson.getVersion(), Person.class);
    }

    public Person updatePerson(Person person) {
        Person storedPerson = kafkaStore.findByIdAndVersionWaitForResul(person.getId(), person.getVersion(),
                Person.class);
        updatePerson(person, storedPerson);
        return storedPerson;
    }

    public void updatePerson(Person person, Person storedPerson) {
        storedPerson.updatePersonalData(person.getFirstname(), person.getLastname(), person.getBirthday());
        kafkaStore.publishEvents(storedPerson.getUncommitedEvents());
        storedPerson.clearEvents();
    }
}