package ch.adesso.partyservice.party.entity;

import org.apache.avro.reflect.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {

	private String contactId;
	
	private Address address;
	
	@Nullable
	private ContactTypeEnum contactType;
}
