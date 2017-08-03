package ch.adesso.partyservice.party.entity;

import java.util.Collection;

import ch.adesso.partyservice.party.event.CoreEvent;

public interface AggregateRoot{

	public long getVersion();
	public Collection<CoreEvent> getUncommitedEvents();

	public void applyEvent(final CoreEvent event);
	public void applyChange(final CoreEvent event);

}
