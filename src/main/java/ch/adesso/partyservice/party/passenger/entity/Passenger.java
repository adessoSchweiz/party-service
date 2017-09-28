package ch.adesso.partyservice.party.passenger.entity;

import ch.adesso.partyservice.AggregateRoot;
import ch.adesso.partyservice.NullAwareJsonObjectBuilder;
import ch.adesso.partyservice.party.passenger.entity.event.*;
import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.logging.Logger;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Passenger extends AggregateRoot {
    private static final Logger LOG = Logger.getLogger(Passenger.class.getName());

    private String firstname;
    private String lastname;
    private String birthday;
    private PartyStatus status;
    private String mobil;
    private String email;
    private Address address;
    private CreditCard creditCard;

    public enum JSON_KEYS {
        ID("id"),
        FIRSTNAME("firstname"),
        LASTNAME("lastname"),
        BIRTHDAY("birthday"),
        STATUS("status"),
        MOBIL("mobil"),
        EMAIL("email"),
        ADDRESS("address"),
        CREDIT_CARD("credit_card");

        @Getter
        private String keyName;

        JSON_KEYS(String keyName) {
            this.keyName = keyName;
        }

    }

    public JsonObject toJson() {
        return NullAwareJsonObjectBuilder.wrap(Json.createObjectBuilder())
                .add(JSON_KEYS.ID.getKeyName(), getId())
                .add(JSON_KEYS.FIRSTNAME.getKeyName(), getFirstname())
                .add(JSON_KEYS.LASTNAME.getKeyName(), getLastname())
                .add(JSON_KEYS.BIRTHDAY.getKeyName(), getBirthday())
                .add(JSON_KEYS.STATUS.getKeyName(), (getStatus() == null) ? null : getStatus().name())
                .add(JSON_KEYS.MOBIL.getKeyName(), getMobil())
                .add(JSON_KEYS.EMAIL.getKeyName(), getEmail())
                .add(JSON_KEYS.ADDRESS.getKeyName(), getAddress().toJson())
                .add(JSON_KEYS.CREDIT_CARD.getKeyName(), getCreditCard().toJson())
                .build();
    }

    public Passenger(JsonObject passenger) {
        setId(passenger.getString(JSON_KEYS.ID.getKeyName(), null));
        setFirstname(passenger.getString(JSON_KEYS.FIRSTNAME.getKeyName(), null));
        setLastname(passenger.getString(JSON_KEYS.LASTNAME.getKeyName(), null));
        setBirthday(passenger.getString(JSON_KEYS.BIRTHDAY.getKeyName(), null));
        setStatus(PartyStatus.valueOf(passenger.getString(JSON_KEYS.STATUS.getKeyName(), null)));
        setMobil(passenger.getString(JSON_KEYS.MOBIL.getKeyName(), null));
        setEmail(passenger.getString(JSON_KEYS.EMAIL.getKeyName(), null));
        setAddress(new Address(passenger.getJsonObject(JSON_KEYS.ADDRESS.getKeyName())));
        setCreditCard(new CreditCard(passenger.getJsonObject(JSON_KEYS.CREDIT_CARD.getKeyName())));
    }

    public Passenger(String id) {
        applyChange(new PassengerCreatedEvent(id));
    }

    private void applyEvent(PassengerCreatedEvent event) {
        setId(event.getAggregateId());
        setVersion(event.getSequence());
    }

    private void applyEvent(PassengerChangedEvent event) {
        setVersion(event.getSequence());
        setFirstname(event.getFirstname());
        setLastname(event.getLastname());
        setBirthday(event.getBirthday());
        setStatus(PartyStatus.valueOf(event.getStatus()));
        setMobil(event.getMobil());
        setEmail(event.getEmail());
    }

    private void applyEvent(CreditCardCreatedEvent event) {
        setVersion(event.getSequence());
        CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
                event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

        setCreditCard(creditCard);
    }


    private void applyEvent(CreditCardChangedEvent event) {
        setVersion(event.getSequence());
        CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
                event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

        setCreditCard(creditCard);
    }

    private void applyEvent(AddressCreatedEvent event) {
        setVersion(event.getSequence());
        Address address = new Address(event.getStreet(), event.getHouseNumber(), event.getZip(), event.getCity(), event.getCountry());
        setAddress(address);
    }


    public void updatePassengerData(Passenger person) {
        applyChange(new PassengerChangedEvent(getId(), getNextVersion(), person.getFirstname(), person.getLastname(), person.getBirthday(), person.getStatus(), person.getMobil(), person.getEmail()));
    }

    public void updateAddress(Address address) {
        applyChange(new AddressCreatedEvent(
                getId(),
                getNextVersion(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getZip(),
                address.getCity(),
                address.getCountry()));
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
