package ch.adesso.partyservice.logging.boundary;

import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;

import ch.adesso.partyservice.CoreEvent;
import ch.adesso.partyservice.ProcessorEvent;
import ch.adesso.partyservice.party.passenger.entity.event.PassengerCreatedEvent;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EventLogger {

	private static final Logger LOG = Logger.getLogger(EventLogger.class.getName());

	public void logEvent(@Observes CoreEvent event) {
		LOG.info("Event: " + event + " sucessfully processed.");
	}

	public void logEvent(@Observes PassengerCreatedEvent event) {
		LOG.info("Passenger created, ID: " + event.getAggregateId() + " Version: " + event.getSequence());
	}

	public void logEvent(@Observes ProcessorEvent event) {
		LOG.info("Processor processed event: " + event.getAggregateId() + " LastEvent: " + event.getProcessedVersion());
	}
}
