package ch.adesso.partyservice.party.person.passenger.entity;

import ch.adesso.partyservice.party.Party;
import ch.adesso.partyservice.party.PartyRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Passenger extends PartyRole {

    @Nullable
    private CreditCard creditCard;

    public Passenger(String id, Party party) {
        super();
        this.setId(id);
        this.setParty(party);
    }

}
