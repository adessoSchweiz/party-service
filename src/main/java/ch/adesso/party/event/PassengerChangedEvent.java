package ch.adesso.party.event;

import org.apache.avro.reflect.Nullable;

import ch.adesso.party.entity.PartyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerChangedEvent extends PartyEvent {

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

	public PassengerChangedEvent(String aggregateId, long sequence, String firstname, String lastname, String birthday,
			PartyStatus status, String mobile, String email) {
		super(PassengerChangedEvent.class, aggregateId, sequence);
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthday = birthday;
		this.status = status != null ? status.name() : null;
		this.mobil = mobile;
		this.email = email;
	}
}
