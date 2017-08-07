package ch.adesso.partyservice.party.entity;

import java.sql.Driver;

import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.Union;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = Passenger.class, name = "passenger"),
		@Type(value = Driver.class, name = "driver") })
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@Union({Passenger.class, Driver.class})
public abstract class PartyRole extends AggregateRoot {
	
	@Nullable
	private Party party;
	
	private String login;
	private String password;
}
