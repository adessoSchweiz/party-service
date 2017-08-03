package ch.adesso.partyservice.party.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CoreEvent {
    private String aggregateId;
    private String eventType;
    private long timestamp;
    private long sequence;

    public CoreEvent(Class<?> eventType, String aggregateId) {
        this.timestamp = System.nanoTime();
        this.eventType = eventType.getName();
        this.aggregateId = aggregateId;
    }
}