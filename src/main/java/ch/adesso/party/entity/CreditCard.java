package ch.adesso.party.entity;

import javax.json.Json;
import javax.json.JsonObject;

import ch.adesso.party.NullAwareJsonObjectBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditCard {
	private String cardNumber;
	private CreditCardTypeEnum cardType;
	private String nameOnCard;
	private int validToMonth;
	private int validToYear;
	private int secretNumber;

	public enum JSON_KEYS {
		CARD_NUMBER("card_number"), CARD_TYPE("card_type"), NAME_ON_CARD("name_on_card"), VALID_TO_MONTH(
				"valid_to_month"), VALID_TO_YEAR("valid_to_year"), SECRET_NUMBER("secret_number");

		@Getter
		private String keyName;

		JSON_KEYS(String keyName) {
			this.keyName = keyName;
		}
	}

	public CreditCard(JsonObject creditCard) {
		setCardNumber(creditCard.getString(JSON_KEYS.CARD_NUMBER.getKeyName()));
		setCardType(CreditCardTypeEnum.valueOf(creditCard.getString(JSON_KEYS.CARD_TYPE.getKeyName())));
		setNameOnCard(creditCard.getString(JSON_KEYS.NAME_ON_CARD.getKeyName()));
		setValidToMonth(creditCard.getInt(JSON_KEYS.VALID_TO_MONTH.getKeyName()));
		setValidToYear(creditCard.getInt(JSON_KEYS.VALID_TO_YEAR.getKeyName()));
		setSecretNumber(creditCard.getInt(JSON_KEYS.SECRET_NUMBER.getKeyName()));
	}

	public JsonObject toJson() {
		return NullAwareJsonObjectBuilder.wrap(Json.createObjectBuilder())
				.add(JSON_KEYS.CARD_NUMBER.getKeyName(), getCardNumber())
				.add(JSON_KEYS.CARD_TYPE.getKeyName(), getCardType().name())
				.add(JSON_KEYS.NAME_ON_CARD.getKeyName(), getNameOnCard())
				.add(JSON_KEYS.VALID_TO_MONTH.getKeyName(), getValidToMonth())
				.add(JSON_KEYS.VALID_TO_YEAR.getKeyName(), getValidToYear())
				.add(JSON_KEYS.SECRET_NUMBER.getKeyName(), getSecretNumber()).build();
	}
}
