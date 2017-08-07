package ch.adesso.partyservice.party.entity;

import org.apache.avro.reflect.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString(callSuper=true)
public class PostalAddress extends Address {
	
    private String street;
    
	@Nullable
    private String houseNo;
    
    private String city;
    private String postalcode;
    private String country;
    
	public PostalAddress(String addressId, String street, String houseNo, String city, String postalcode, String country) {
		super();
		this.setAddressId(addressId);
		this.street = street;
		this.houseNo = houseNo;
		this.city = city;
		this.postalcode = postalcode;
		this.country = country;
	}
    
    
    
}
