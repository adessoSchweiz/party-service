package ch.adesso.partyservice.party.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class PersonCreatedCommand extends CoreCommand {

    private String firstname;
    private String lastname;
    
    public PersonCreatedCommand( String aggregateId, long sequence, String firstname, String lastname) {
        super(aggregateId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
