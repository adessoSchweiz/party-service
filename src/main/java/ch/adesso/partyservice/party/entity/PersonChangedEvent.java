package ch.adesso.partyservice.party.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.AvroDefault;

/**
 * Created by tom on 23.06.17.
 */
@Data
@ToString
@NoArgsConstructor
public class PersonChangedEvent extends PartyEvent {

    @AvroDefault("null")
    private String firstname;
    
    @AvroDefault("null")
    private String lastname;
    
    public PersonChangedEvent(String partyId, long sequence, String firstname, String lastname) {
        super(PersonChangedEvent.class, partyId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
