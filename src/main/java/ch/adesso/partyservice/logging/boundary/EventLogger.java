package ch.adesso.partyservice.logging.boundary;

import ch.adesso.partyservice.CoreEvent;
import ch.adesso.partyservice.party.passenger.entity.event.PassengerCreatedEvent;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

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
}
