package ch.adesso.partyservice.party.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.Union;

/**
 * Created by tom on 23.06.17.
 */
@Data
@NoArgsConstructor
@Union({PersonCreatedEvent.class, PersonChangedEvent.class})
public class PartyEvent extends CoreEvent {

    public PartyEvent(Class<?> eventType, String streamId, long sequence) {
        super(eventType, streamId, sequence);
    }
}
