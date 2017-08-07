package ch.adesso.partyservice.party.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@NoArgsConstructor
public class PassengerCreatedEvent extends PartyEvent {

    private String login;
    private String password;
    
    public PassengerCreatedEvent(String aggregateId,  String login, String password) {
        super(PassengerCreatedEvent.class, aggregateId);
        this.login = login;
        this.password = password;
    }
}
