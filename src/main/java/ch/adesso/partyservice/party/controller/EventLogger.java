package ch.adesso.partyservice.party.controller;

import javax.enterprise.event.Observes;

import ch.adesso.partyservice.party.event.CoreEvent;
import ch.adesso.partyservice.party.event.PersonCreatedEvent;

public class EventLogger {

	public void logEvent(@Observes CoreEvent event) {
		System.out.println("Event: " + event + " sucessfully processed.");
	}

	public void logEvent(@Observes PersonCreatedEvent event) {
		System.out.println("Person created, ID: " + event.getAggregateId() + " Version: " + event.getSequence());
	}
}
