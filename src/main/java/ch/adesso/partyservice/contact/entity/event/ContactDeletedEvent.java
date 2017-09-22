package ch.adesso.partyservice.contact.entity.event;

import ch.adesso.partyservice.PartyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ContactDeletedEvent extends PartyEvent {

	private String contactId;

	public ContactDeletedEvent(String aggregateId, long sequence, String contactId) {
		super(ContactDeletedEvent.class, aggregateId, sequence);
		this.contactId = contactId;
	}

}
