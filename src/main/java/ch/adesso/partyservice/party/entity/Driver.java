package ch.adesso.partyservice.party.entity;

import java.util.Collection;

import ch.adesso.partyservice.party.event.CoreEvent;

/**
 * 
 * AggregateRoot
 * 
 *
 */
public class Driver extends PartyRole implements AggregateRoot {

	@Override
	public long getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<CoreEvent> getUncommitedEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyEvent(CoreEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyChange(CoreEvent event) {
		// TODO Auto-generated method stub
		
	}

}
