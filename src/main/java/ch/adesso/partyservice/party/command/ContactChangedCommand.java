package ch.adesso.partyservice.party.command;

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
public class ContactChangedCommand extends CoreCommand {

	private String contactId;
	private Address address;
	
	@AvroDefault("null")
	private ContactTypeEnum contactType;
	
	public ContactChangedCommand(String aggregateId, long sequence, String contactId, Address address,
			ContactTypeEnum contactType) {
		super(aggregateId, sequence);
		this.contactId = contactId;
		this.address = address;
		this.contactType = contactType;
	}
	
}
