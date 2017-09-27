package ch.adesso.partyservice.party.passenger.entity;

import ch.adesso.partyservice.AggregateRoot;
import ch.adesso.partyservice.CoreEvent;
import ch.adesso.partyservice.party.passenger.PersonCreatedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Collection;

@Data
@EqualsAndHashCode
@ToString
public class Passenger extends AggregateRoot {
    @Nullable
    private String firstname;
    @Nullable
    private String lastname;
    @Nullable
    private String birthday;
    @Nullable
    private String status;
    @Nullable
    private String mobil;
    @Nullable
    private String email;
    @Nullable
    private Address address;
    @Nullable
    private CreditCard creditCard;

    public void applyEvent(PersonCreatedEvent event) {
        setId(event.getAggregateId());
        setVersion(event.getSequence());
    }

    public enum JSON_KEYS {
        ID("id"), FIRSTNAME("firstname"), LASTNAME("lastname"), BIRTHDAY("birthday"), STATUS("status"), MOBIL("mobil"), EMAIL("email"), ADDRESS("address");

        @Getter
        private String name;

        JSON_KEYS(String name) {
            this.name = name;
        }

    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.ID.getName(), getId())
                .add(JSON_KEYS.FIRSTNAME.getName(), getFirstname())
                .add(JSON_KEYS.LASTNAME.getName(), getLastname())
                .add(JSON_KEYS.BIRTHDAY.getName(), getBirthday())
                .add(JSON_KEYS.STATUS.getName(), getStatus())
                .add(JSON_KEYS.MOBIL.getName(), getMobil())
                .add(JSON_KEYS.EMAIL.getName(), getEmail())
                .add(JSON_KEYS.ADDRESS.getName(), getAddress().toJson())
                .build();
    }

    public Passenger(Passenger person) {
        setId(person.getId());
        setFirstname(person.getFirstname());
        setLastname(person.getLastname());
        setBirthday(person.getBirthday());
        setStatus(person.getStatus());
        setMobil(person.getMobil());
        setEmail(person.getEmail());
        setAddress(new Address(person.getAddress()));
    }

    public Passenger(JsonObject person) {
        setId(person.getString(JSON_KEYS.ID.getName()));
        setFirstname(person.getString(JSON_KEYS.FIRSTNAME.getName()));
        setLastname(person.getString(JSON_KEYS.LASTNAME.getName()));
        setBirthday(person.getString(JSON_KEYS.BIRTHDAY.getName()));
        setStatus(person.getString(JSON_KEYS.STATUS.getName()));
        setMobil(person.getString(JSON_KEYS.MOBIL.getName()));
        setEmail(person.getString(JSON_KEYS.EMAIL.getName()));
        setAddress(new Address(person.getJsonObject(JSON_KEYS.ADDRESS.getName())));
    }

    public Passenger(Collection<CoreEvent> events) {
        events.stream().forEach(this::applyEvent);
    }

    public Passenger(String id) {
        applyChange(new PassengerCreatedEvent(id));
    }

    public void applyEvent(PassengerCreatedEvent event) {
        setId(event.getAggregateId());
        setVersion(event.getSequence());
    }

    public void applyEvent(PassengerChangedEvent event) {
        setVersion(event.getSequence());
        setFirstname(event.getFirstname());
        setLastname(event.getLastname());
        setBirthday(event.getBirthday());
        setStatus(event.getStatus());
        setMobil(event.getMobil());
        setEmail(event.getEmail());
    }

    public void applyEvent(CreditCardCreatedEvent event) {
        setVersion(event.getSequence());
        CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
                event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

        setCreditCard(creditCard);
    }


    public void applyEvent(CreditCardChangedEvent event) {
        setVersion(event.getSequence());
        CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
                event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

        setCreditCard(creditCard);
    }

    public void applyEvent(AddressCreatedEvent event) {
        setVersion(event.getSequence());
        Address address = new Address(event.getStreet(), event.getHouseNo(), event.getZip(), event.getCity(), event.getCountry());
        setAddress(address);
    }


    public void updatePassengerData(Passenger person) {
        applyChange(new PassengerChangedEvent(getId(), getNextVersion(), person.getFirstname(), person.getLastname(), person.getBirthday(), person.getStatus(), person.getMobil(), person.getEmail()));
    }


    public void updateCreditCard(CreditCard creditCard) {
        if (creditCard == null) {
            return;
        }

        if (getCreditCard() == null) {
            CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getId(), getNextVersion(),
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
                CreditCardCreatedEvent creditCardCreatedEvent = new CreditCardCreatedEvent(getId(), getNextVersion(),
                        creditCard.getCardNumber(), creditCard.getCardType(), creditCard.getNameOnCard(),
                        creditCard.getValidToMonth(), creditCard.getValidToYear(), creditCard.getSecretNumber());

                applyChange(creditCardCreatedEvent);
            }
        }
    }
}
