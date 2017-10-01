package ch.adesso.party.logging;

import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;

import ch.adesso.party.CoreEvent;
import ch.adesso.party.event.PassengerCreatedEvent;
import ch.adesso.party.kafka.KafkaProcessorEvent;

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

	public void logEvent(@Observes KafkaProcessorEvent event) {
		LOG.info("Processor processed event AggregateId: " + event.getAggregateId() + " processed event version: "
				+ event.getProcessedVersion());
	}
}
