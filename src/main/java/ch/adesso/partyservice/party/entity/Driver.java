package ch.adesso.partyservice.party.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import ch.adesso.partyservice.party.event.CoreEvent;

/**
 * 
 * AggregateRoot
 * 
 *
 */
@JsonTypeInfo(include=JsonTypeInfo.As.WRAPPER_OBJECT, use=Id.NAME)
public class Driver extends PartyRole {

	@Override
	public void applyEvent(CoreEvent event) {
		// TODO Auto-generated method stub
		
	}

}
