package ch.adesso.partyservice.contact.entity;

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
