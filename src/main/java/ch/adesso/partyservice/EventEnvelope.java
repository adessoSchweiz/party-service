package ch.adesso.partyservice;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
