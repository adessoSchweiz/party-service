package ch.adesso.partyservice.party.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class PassengerChangedCommand extends CoreCommand {
	
    private String login;
    private String password;
    
    public PassengerChangedCommand(String aggregateId, long sequence, String login, String password) {
        super(aggregateId, sequence);
        this.login = login;
        this.password = password;
    }
}
