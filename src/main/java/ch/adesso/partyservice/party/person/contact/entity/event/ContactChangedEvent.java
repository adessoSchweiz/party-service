package ch.adesso.partyservice.party.person.contact.entity.event;

import ch.adesso.partyservice.party.PartyEvent;
import ch.adesso.partyservice.party.person.contact.entity.Address;
import ch.adesso.partyservice.party.person.contact.entity.ContactTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ContactChangedEvent extends PartyEvent {

    private String contactId;
    private Address address;

    @Nullable
    private ContactTypeEnum contactType;

    public ContactChangedEvent(String aggregateId, long sequence, String contactId, Address address,
                               ContactTypeEnum contactType) {
        super(ContactChangedEvent.class, aggregateId, sequence);
        this.contactId = contactId;
        this.address = address;
        this.contactType = contactType;
    }

}
