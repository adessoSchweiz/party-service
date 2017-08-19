package ch.adesso.partyservice.party.entity;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Passenger extends PartyRole {

	@Nullable
	private CreditCard creditCard;

	public Passenger(String id, Party party) {
		super();
		this.setId(id);
		this.setParty(party);
	}

}
