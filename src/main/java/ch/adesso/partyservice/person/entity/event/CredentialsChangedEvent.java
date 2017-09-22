package ch.adesso.partyservice.person.entity.event;

import ch.adesso.partyservice.PartyEvent;
import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CredentialsChangedEvent extends PartyEvent {

	@Nullable
	private String login;

	@Nullable
	private String password;

	public CredentialsChangedEvent(String aggregateId, long sequence, String login, String password) {
		super(CredentialsChangedEvent.class, aggregateId, sequence);
		this.login = login;
		this.password = password;
	}
}
