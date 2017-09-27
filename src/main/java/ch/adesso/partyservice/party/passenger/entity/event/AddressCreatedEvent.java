package ch.adesso.partyservice.party.passenger.entity.event;

import ch.adesso.partyservice.party.PartyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AddressCreatedEvent extends PartyEvent {
    @Nullable
    private String street;
    @Nullable
    private String houseNumber;
    @Nullable
    private String zip;
    @Nullable
    private String city;
    @Nullable
    private String country;

    public AddressCreatedEvent(String aggregateId, long sequence, String street, String houseNumber, String zip, String city, String country) {
        super(AddressCreatedEvent.class, aggregateId, sequence);
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }

}
