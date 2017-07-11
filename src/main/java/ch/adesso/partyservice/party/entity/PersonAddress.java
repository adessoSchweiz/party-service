package ch.adesso.partyservice.party.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class PersonAddress {

    private Person person;
    private Address address;

}
