package ch.adesso.partyservice.health.boundary;

import ch.adesso.partyservice.party.entity.*;
import ch.adesso.partyservice.person.entity.Person;

import java.util.UUID;

public class DummyProvider {

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

}
