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
    private String birthday;
    
    public PersonCreatedCommand( String aggregateId, long sequence, String firstname, String lastname, String birthday) {
        super(aggregateId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }
}
