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
    private String eventId;
    private String eventType;
    private long timestamp;

    public CoreEvent(Class<?> eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = System.nanoTime();
        this.eventType = eventType.getName();
    }
}