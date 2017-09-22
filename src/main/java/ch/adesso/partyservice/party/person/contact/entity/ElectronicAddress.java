package ch.adesso.partyservice.party.person.contact.entity;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ElectronicAddress extends Address {

    private ElectronicAddressTypeEnum electronicType;
    private String value;
}
