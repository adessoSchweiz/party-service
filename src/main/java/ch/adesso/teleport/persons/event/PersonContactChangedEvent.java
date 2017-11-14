package ch.adesso.teleport.persons.event;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonContactChangedEvent extends PersonEvent {

	@Nullable
	private String mobil;
	@Nullable
	private String email;

	public PersonContactChangedEvent(String aggregateId, long sequence, String mobile, String email) {
		super(PersonContactChangedEvent.class, aggregateId, sequence);
		this.mobil = mobile;
		this.email = email;
	}
}
