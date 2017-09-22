package ch.adesso.partyservice.party;

import avro.shaded.com.google.common.collect.Lists;
import ch.adesso.partyservice.AggregateRoot;
import ch.adesso.partyservice.party.organization.entiy.Organization;
import ch.adesso.partyservice.party.person.Person;
import ch.adesso.partyservice.party.person.contact.entity.Contact;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.Union;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Union({Person.class, Organization.class})
public abstract class Party extends AggregateRoot {

    @Nullable
    private List<PartyRole> partyRoles;

    @Nullable
    private List<Contact> contacts;

    public Party() {
        partyRoles = Lists.newArrayList();
        contacts = Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
    public <T extends PartyRole> T getPartyRole(Class<T> clazz) {
        for (PartyRole r : getPartyRoles()) {
            if (r.getClass().isAssignableFrom(clazz)) {
                return (T) r;
            }
        }
        return null;
    }

    public void addPartyRole(PartyRole partyRole) {
        if (partyRoles == null) {
            partyRoles = new ArrayList<>();
        }
        partyRoles.add(partyRole);
    }

    public void deletePartyRole(PartyRole partyRole) {
        if (partyRoles != null) {
            partyRoles.remove(partyRole);
        }
    }

    public void addContact(Contact contact) {
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        contacts.add(contact);
    }

    public void deleteContact(Contact contact) {
        if (contacts != null) {
            contacts.remove(contact);
        }
    }
}
