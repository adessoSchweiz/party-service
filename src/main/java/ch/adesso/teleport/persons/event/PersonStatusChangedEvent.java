package ch.adesso.teleport.persons.event;

import org.apache.avro.reflect.Nullable;

import ch.adesso.teleport.persons.entity.PersonStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonStatusChangedEvent extends PersonEvent {

	@Nullable
	private String status;

	public PersonStatusChangedEvent(String aggregateId, long sequence, PersonStatus status) {
		super(PersonStatusChangedEvent.class, aggregateId, sequence);
		this.status = status != null ? status.toString() : null;
	}
}
