package ch.adesso.partyservice.contact.entity;

import ch.adesso.partyservice.contact.entity.event.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
public class ElectronicAddress extends Address {

	private ElectronicAddressTypeEnum electronicType;
	private String value;
}
