package ch.adesso.partyservice.party.entity;

import java.util.List;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class Person extends Party {

	@Nullable
    private String firstname;
    
	@Nullable
    private String lastname;
    
    public Person(List<Contact> contacts, String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.setContacts(contacts);
	}
}
