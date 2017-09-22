package ch.adesso.partyservice.party.person.passenger.boundary;

import ch.adesso.partyservice.eventstore.boundary.KafkaStore;
import ch.adesso.partyservice.person.entity.Person;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.UUID;

@Stateless
public class PassengerService {

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