package ch.adesso.partyservice.party.event;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class PersonCreatedEvent extends PartyEvent {

	@Nullable
    private String firstname;
    
	@Nullable
    private String lastname;
	
	@Nullable
	private String birthday;
    
    public PersonCreatedEvent( String aggregateId, String firstname, String lastname, String birthday) {
        super(PersonCreatedEvent.class, aggregateId);
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }
}
