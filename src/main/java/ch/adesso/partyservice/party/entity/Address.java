package ch.adesso.partyservice.party.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Address {
    private String id;
    private String street;
    private String city;
    private String partyId;
}
