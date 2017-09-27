package ch.adesso.partyservice.party.passenger.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditCardTypeEnum {
	MASTER_CARD, VISA, AMEX;

	@JsonValue
	public int toValue() {
		return ordinal();
	}
}
