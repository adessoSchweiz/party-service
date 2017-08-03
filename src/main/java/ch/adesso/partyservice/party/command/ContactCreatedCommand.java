package ch.adesso.partyservice.party.command;

import java.util.UUID;

import org.apache.avro.reflect.AvroDefault;

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
public class ContactCreatedCommand extends CoreCommand {

	private String contactId;
	private Address address;
	
	@AvroDefault("null")
	private ContactTypeEnum contactType;
	
	public ContactCreatedCommand( String aggregateId, long sequence, Address address,
			ContactTypeEnum contactType) {
		super(aggregateId, sequence);
		this.contactId = UUID.randomUUID().toString();
		this.address = address;
		this.contactType = contactType;
	}
	
}
