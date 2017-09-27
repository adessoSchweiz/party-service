package ch.adesso.partyservice.party.passenger.entity.event;

import ch.adesso.partyservice.party.PartyEvent;
import ch.adesso.partyservice.party.passenger.entity.PartyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerChangedEvent extends PartyEvent {

    @Nullable
    private String firstname;
    @Nullable
    private String lastname;
    @Nullable
    private String birthday;
    @Nullable
    private PartyStatus status;
    @Nullable
    private String mobil;
    @Nullable
    private String email;

    public PassengerChangedEvent(String aggregateId, long sequence, String firstname, String lastname, String birthday, PartyStatus status, String mobile, String email) {
        super(PassengerChangedEvent.class, aggregateId, sequence);
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.status = status;
        this.mobil = mobile;
        this.email = email;
    }
}
