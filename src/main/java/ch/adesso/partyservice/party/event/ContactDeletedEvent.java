package ch.adesso.partyservice.party.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class ContactDeletedEvent extends PartyEvent {

	private String contactId;
	
	public ContactDeletedEvent(String aggregateId, String contactId) {
		super(ContactDeletedEvent.class, aggregateId);
		this.contactId = contactId;
	}
	
}
