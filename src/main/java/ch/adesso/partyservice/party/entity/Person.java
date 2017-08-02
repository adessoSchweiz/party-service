package ch.adesso.partyservice.party.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.AvroDefault;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {

    private String partyId;
    
    @AvroDefault("null")
    private String firstname;
    
    @AvroDefault("null")
    private String lastname;

    @AvroDefault("0")
    private long version = 0;

    public Person applyEvent(PartyEvent event) {
    	System.out.println("apply PersonEvent: " + event);
    	if(event instanceof PersonCreatedEvent) {
            applyEvent((PersonCreatedEvent)event);
        } else if((event instanceof PersonChangedEvent)) {
            applyEvent((PersonChangedEvent) event);
        }
        return this;
    }

    public Person applyEvent(PersonChangedEvent event) {
    	System.out.println("apply PersonChangedEvent: " + event);
        firstname = event.getFirstname();
        lastname = event.getLastname();
        version = event.getSequence();
        return this;
    }

    public Person applyEvent(PersonCreatedEvent event) {
    	System.out.println("apply PersonCreatedEvent: " + event);
        firstname = event.getFirstname();
        lastname = event.getLastname();
        partyId = event.getStreamId();
        version = event.getSequence();
        return this;
    }

}
