package ch.adesso.partyservice.party.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Created by tom on 11.06.17.
 */
@Data
@NoArgsConstructor
@ToString
public class CoreEvent {
    private String streamId;
    private String eventType;
    private long timestamp;
    private long sequence;

    public CoreEvent(Class<?> eventType, String streamId, long sequence) {
        this.timestamp = System.nanoTime();
        this.eventType = eventType.getName();
        this.streamId = streamId;
        this.sequence = sequence;
    }
}