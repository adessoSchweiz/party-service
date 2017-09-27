package ch.adesso.partyservice;

import ch.adesso.partyservice.party.PartyEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@NoArgsConstructor
@ToString
@Data
public class EventEnvelope {
    @Nullable
    private Header header;

    @Nullable
    private PartyEvent event;

    public EventEnvelope(PartyEvent event) {
        this.event = event;
    }

}
