package ch.adesso.partyservice.party.person.contact.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.Data;
import org.apache.avro.reflect.Union;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = PostalAddress.class, name = "postalAddress"),
        @Type(value = ElectronicAddress.class, name = "electronicAddress")})
@Data
@Union({PostalAddress.class, ElectronicAddress.class})
public abstract class Address {
    private String addressId;
}
