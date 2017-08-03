package ch.adesso.partyservice.party.event;

import java.util.UUID;

import org.apache.avro.reflect.Nullable;

import ch.adesso.partyservice.party.entity.Address;
import ch.adesso.partyservice.party.entity.ContactTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class ContactCreatedEvent extends PartyEvent {

	private String contactId;
	private Address address;
	
	@Nullable
	private ContactTypeEnum contactType;
	
	public ContactCreatedEvent( String aggregateId, Address address,
			ContactTypeEnum contactType) {
		super(ContactCreatedEvent.class, aggregateId);
		this.contactId = UUID.randomUUID().toString();
		this.address = address;
		this.contactType = contactType;
	}
	
}
