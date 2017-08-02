package ch.adesso.partyservice.party.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.AvroDefault;

@Data
@ToString
@NoArgsConstructor
public class PersonCreatedEvent extends PartyEvent {

    @AvroDefault("null")
    private String firstname;
    
    @AvroDefault("null")
    private String lastname;
    
    public PersonCreatedEvent( String partyId, long sequence, String firstname, String lastname) {
        super(PersonCreatedEvent.class, partyId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
    }

}
