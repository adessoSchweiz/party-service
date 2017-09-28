package ch.adesso.partyservice.party.passenger.entity;

import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import ch.adesso.partyservice.AggregateRoot;
import ch.adesso.partyservice.NullAwareJsonObjectBuilder;
import ch.adesso.partyservice.party.passenger.entity.event.AddressCreatedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.CreditCardChangedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.CreditCardCreatedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.PassengerChangedEvent;
import ch.adesso.partyservice.party.passenger.entity.event.PassengerCreatedEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
		ID("id"), VERSION("version"), FIRSTNAME("firstname"), LASTNAME("lastname"), BIRTHDAY("birthday"), STATUS(
				"status"), MOBIL("mobil"), EMAIL("email"), ADDRESS("address"), CREDIT_CARD("credit_card");

		@Getter
		private String keyName;

		JSON_KEYS(String keyName) {
			this.keyName = keyName;
		}

	}

	public JsonObject toJson() {
		return NullAwareJsonObjectBuilder.wrap(Json.createObjectBuilder())
				.add(JSON_KEYS.ID.getKeyName(), getId())
				.add(JSON_KEYS.VERSION.getKeyName(), getVersion())
				.add(JSON_KEYS.FIRSTNAME.getKeyName(), getFirstname())
				.add(JSON_KEYS.LASTNAME.getKeyName(), getLastname()).add(JSON_KEYS.BIRTHDAY.getKeyName(), getBirthday())
				.add(JSON_KEYS.STATUS.getKeyName(), (getStatus() == null) ? null : getStatus().name())
				.add(JSON_KEYS.MOBIL.getKeyName(), getMobil()).add(JSON_KEYS.EMAIL.getKeyName(), getEmail())
				.add(JSON_KEYS.ADDRESS.getKeyName(), (getAddress() == null) ? null : getAddress().toJson())
				.add(JSON_KEYS.CREDIT_CARD.getKeyName(), (getCreditCard() == null) ? null : getCreditCard().toJson())
				.build();
	}

	public Passenger(JsonObject passenger) {
		String statusText = passenger.getString(JSON_KEYS.STATUS.getKeyName(), null);
		JsonValue addressJO = passenger.get(JSON_KEYS.ADDRESS.getKeyName());
		JsonValue creditCardJO = passenger.get(JSON_KEYS.CREDIT_CARD.getKeyName());

		setId(passenger.getString(JSON_KEYS.ID.getKeyName(), null));
		setVersion(passenger.getInt(JSON_KEYS.VERSION.getKeyName(), 0));
		setFirstname(passenger.getString(JSON_KEYS.FIRSTNAME.getKeyName(), null));
		setLastname(passenger.getString(JSON_KEYS.LASTNAME.getKeyName(), null));
		setBirthday(passenger.getString(JSON_KEYS.BIRTHDAY.getKeyName(), null));
		setStatus((statusText == null) ? null : PartyStatus.valueOf(statusText));
		setMobil(passenger.getString(JSON_KEYS.MOBIL.getKeyName(), null));
		setEmail(passenger.getString(JSON_KEYS.EMAIL.getKeyName(), null));
		setAddress(
				(addressJO == null || addressJO.equals(JsonValue.NULL)) ? null : new Address((JsonObject) addressJO));
		setCreditCard((creditCardJO == null || creditCardJO.equals(JsonValue.NULL)) ? null
				: new CreditCard((JsonObject) creditCardJO));
	}

	public Passenger(String id) {
		applyChange(new PassengerCreatedEvent(id));
	}

	private void applyEvent(PassengerCreatedEvent event) {
		setId(event.getAggregateId());
	}

	private void applyEvent(PassengerChangedEvent event) {
		setFirstname(event.getFirstname());
		setLastname(event.getLastname());
		setBirthday(event.getBirthday());
		setStatus(event.getStatus() != null ? PartyStatus.valueOf(event.getStatus()) : null);
		setMobil(event.getMobil());
		setEmail(event.getEmail());
	}

	private void applyEvent(CreditCardCreatedEvent event) {
		CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
				event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

		setCreditCard(creditCard);
	}

	private void applyEvent(CreditCardChangedEvent event) {
		CreditCard creditCard = new CreditCard(event.getCardNumber(), event.getCardType(), event.getNameOnCard(),
				event.getValidToMonth(), event.getValidToYear(), event.getSecretNumber());

		setCreditCard(creditCard);
	}

	private void applyEvent(AddressCreatedEvent event) {
		Address address = new Address(event.getStreet(), event.getHouseNumber(), event.getZip(), event.getCity(),
				event.getCountry());
		setAddress(address);
	}

	public void updatePassengerData(Passenger person) {
		applyChange(new PassengerChangedEvent(getId(), getNextVersion(), person.getFirstname(), person.getLastname(),
				person.getBirthday(), person.getStatus(), person.getMobil(), person.getEmail()));
	}

	public void updateAddress(Address address) {
		applyChange(new AddressCreatedEvent(getId(), getNextVersion(), address.getStreet(), address.getHouseNumber(),
				address.getZip(), address.getCity(), address.getCountry()));
	}

	public void updateCreditCard(CreditCard creditCard) {
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
