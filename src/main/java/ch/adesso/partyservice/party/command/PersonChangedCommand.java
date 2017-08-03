package ch.adesso.partyservice.party.command;

import org.apache.avro.reflect.AvroDefault;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class PersonChangedCommand extends CoreCommand {
	
    @AvroDefault("null")
    private String firstname;
    
    @AvroDefault("null")
    private String lastname;
    
    public PersonChangedCommand( String aggregateId, long sequence, String firstname, String lastname) {
        super(aggregateId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
