package ch.adesso.partyservice.party.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContactTypeEnum {

	DOMICILE, CORRESPONDENCE;

	@JsonValue
	public int toValue() {
		return ordinal();
	}
}
