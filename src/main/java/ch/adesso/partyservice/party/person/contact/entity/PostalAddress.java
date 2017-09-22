package ch.adesso.partyservice.party.person.contact.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class PostalAddress extends Address {

    private String street;

    @Nullable
    private String houseNo;

    private String city;
    private String postalcode;
    private String country;

}
