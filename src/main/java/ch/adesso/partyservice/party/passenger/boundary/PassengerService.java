package ch.adesso.partyservice.party.passenger.boundary;

import ch.adesso.partyservice.eventstore.boundary.KafkaStore;
import ch.adesso.partyservice.party.passenger.entity.Passenger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
public class PassengerService {
    private static final Logger LOG = Logger.getLogger(PassengerService.class.getName());

    @Inject
    private KafkaStore kafkaStore;

    public Passenger createPassenger(Passenger passenger) {
        LOG.info("create Passenger Service");
        String passengerId = UUID.randomUUID().toString();
        Passenger newPerson = new Passenger(passengerId);
        updatePassenger(passenger, newPerson);
        return kafkaStore.findByIdAndVersionWaitForResult(newPerson.getId(), newPerson.getVersion(), Passenger.class);
    }

    public Passenger updatePassenger(Passenger passenger) {
        Passenger storedPassenger = kafkaStore.findByIdAndVersionWaitForResult(passenger.getId(), passenger.getVersion(),
                Passenger.class);
        updatePassenger(passenger, storedPassenger);
        return storedPassenger;
    }

    public void updatePassenger(Passenger passenger, Passenger storedPassenger) {
        storedPassenger.updatePassengerData(passenger);
        kafkaStore.publishEvents(storedPassenger.getUncommitedEvents());
        storedPassenger.clearEvents();
    }
}