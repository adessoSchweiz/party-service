package ch.adesso.partyservice.person.entity;

import ch.adesso.partyservice.Party;
import ch.adesso.partyservice.PartyRole;
import ch.adesso.partyservice.creditcard.entity.CreditCard;
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
