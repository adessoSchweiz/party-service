package ch.adesso.partyservice.party;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class PartyEventStream {

	@Nullable
	private String aggregateId;

	@AvroDefault("0")
	private long aggregateVersion = 0;

	@Nullable
	private Map<String, PartyEvent> lastEvents;

	public PartyEventStream() {
		lastEvents = new HashMap<String, PartyEvent>();
	}

	public PartyEventStream(String id) {
		this.aggregateId = id;
		lastEvents = new HashMap<String, PartyEvent>();
	}
}
