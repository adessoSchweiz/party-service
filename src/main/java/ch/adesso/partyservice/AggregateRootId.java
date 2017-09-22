package ch.adesso.partyservice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AggregateRootId {
	private String id;
	private long version;
	
	public String getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}
	
}
