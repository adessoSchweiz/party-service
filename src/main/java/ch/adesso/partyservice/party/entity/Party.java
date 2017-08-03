package ch.adesso.partyservice.party.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.Union;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = Person.class, name = "person"),
		@Type(value = Organization.class, name = "organization") })
@Data
@Union({ Person.class, Organization.class })
public abstract class Party {
	
	@Nullable
	private List<Contact> contacts;

	public void addContact(Contact contact) {
		if (contacts == null) {
			contacts = new ArrayList<>();
		}

		contacts.add(contact);
	}

	public void updateContact(Contact contact) {
		if (contacts == null) {
			contacts = new ArrayList<>();
		} else {
			contacts.remove(contact);
		}
		contacts.add(contact);
	}

	public void deleteContact(Contact contact) {
		if (contacts != null) {
			contacts.remove(contact);
		}
	}
}
