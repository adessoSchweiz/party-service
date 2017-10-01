package ch.adesso.party;

import org.apache.avro.reflect.Nullable;

import ch.adesso.party.event.PartyEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class EventEnvelope {
	@Nullable
	private Header header;

	@Nullable
	private PartyEvent event;

	public EventEnvelope(PartyEvent event) {
		this.event = event;
	}
}
