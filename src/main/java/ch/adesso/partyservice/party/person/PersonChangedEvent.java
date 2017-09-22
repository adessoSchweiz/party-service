package ch.adesso.partyservice.party.person;

import ch.adesso.partyservice.party.PartyEvent;
import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonChangedEvent extends PartyEvent {

	@Nullable
	private String firstname;

	@Nullable
	private String lastname;

	@Nullable
	private String birthday;

	public PersonChangedEvent(String aggregateId, long sequence, String firstname, String lastname, String birthday) {
		super(PersonChangedEvent.class, aggregateId, sequence);
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthday = birthday;
	}
}
