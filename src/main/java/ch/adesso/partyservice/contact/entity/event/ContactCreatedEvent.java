package ch.adesso.partyservice.contact.entity.event;

import ch.adesso.partyservice.PartyEvent;
import ch.adesso.partyservice.contact.entity.Address;
import ch.adesso.partyservice.contact.entity.ContactTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ContactCreatedEvent extends PartyEvent {

    private String contactId;
    private Address address;

    @Nullable
    private ContactTypeEnum contactType;

    public ContactCreatedEvent(String aggregateId, long sequence, Address address, ContactTypeEnum contactType) {
        super(ContactCreatedEvent.class, aggregateId, sequence);
        this.contactId = UUID.randomUUID().toString();
        this.address = address;
        this.contactType = contactType;
    }

}
