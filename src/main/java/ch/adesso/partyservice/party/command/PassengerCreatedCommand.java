package ch.adesso.partyservice.party.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PassengerCreatedCommand extends CoreCommand {

	    private String login;
	    private String password;
	    
	    public PassengerCreatedCommand(String aggregateId, long sequence, String login, String password) {
	        super(aggregateId, sequence);
	        this.login = login;
	        this.password = password;
	    }
}
