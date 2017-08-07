package ch.adesso.partyservice.party.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@NoArgsConstructor
public class PassengerChangedEvent extends PartyEvent {
	
    private String login;
    private String password;
    
    public PassengerChangedEvent(String aggregateId, String login, String password) {
        super(PassengerChangedEvent.class, aggregateId);
        this.login = login;
        this.password = password;
    }
}
