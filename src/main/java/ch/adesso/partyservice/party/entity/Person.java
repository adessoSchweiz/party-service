package ch.adesso.partyservice.party.entity;

import ch.adesso.partyservice.LocalDateTimeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {

    private String id;
    private String firstname;
    private String lastname;
    private long version;

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime birthdate;


    public Person applyEvent(String event) {
        //firstname = event.getFirstname();
        //lastname = event.getLastName()
        System.out.println("got event !!!!! " + event);
        return this;
    }

}
