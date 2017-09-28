package ch.adesso.partyservice.party.passenger.boundary;

import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.adesso.partyservice.eventstore.boundary.KafkaStore;
import ch.adesso.partyservice.party.passenger.entity.Passenger;

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
		Passenger storedPassenger = kafkaStore.findByIdAndVersionWaitForResult(passenger.getId(),
				passenger.getVersion(), Passenger.class);
		updatePassenger(passenger, storedPassenger);
		return storedPassenger;
	}

	public void updatePassenger(Passenger passenger, Passenger storedPassenger) {
		storedPassenger.updatePassengerData(passenger);
		if (passenger.getCreditCard() != null) {
			storedPassenger.updateCreditCard(passenger.getCreditCard());
		}
		if (passenger.getAddress() != null) {
			storedPassenger.updateAddress(passenger.getAddress());
		}
		kafkaStore.save(storedPassenger);
	}
}