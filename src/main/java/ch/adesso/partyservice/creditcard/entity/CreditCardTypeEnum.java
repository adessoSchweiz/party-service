package ch.adesso.partyservice.creditcard.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditCardTypeEnum {
	MASTER_CARD, VISA, AMEX;

	@JsonValue
	public int toValue() {
		return ordinal();
	}
}
