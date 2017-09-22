package ch.adesso.partyservice;

import java.sql.Driver;

import ch.adesso.partyservice.party.person.passenger.entity.Passenger;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.Union;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;
import lombok.ToString;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = Passenger.class, name = "passenger"), @Type(value = Driver.class, name = "driver") })
@Data
@ToString(exclude = { "party" })
@Union({ Passenger.class, Driver.class })
public abstract class PartyRole {

	@Nullable
	private String id;

	@JsonIgnore
	@AvroIgnore
	private Party party;
}
