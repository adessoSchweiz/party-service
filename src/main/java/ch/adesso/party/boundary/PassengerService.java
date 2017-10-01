package ch.adesso.party.boundary;

import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;

import ch.adesso.party.AggregateRoot;
import ch.adesso.party.entity.Passenger;
import ch.adesso.party.kafka.KafkaStore;

@Stateless
public class PassengerService {
	private static final Logger LOG = Logger.getLogger(PassengerService.class.getName());

	@Inject
	private KafkaStore kafkaStore;

	public Passenger createPassenger(Passenger passenger) {
		LOG.info("create Passenger Service");
		String passengerId = UUID.randomUUID().toString();
		Passenger newPassenger = new Passenger(passengerId);

		updatePassenger(passenger, newPassenger);
		return find(newPassenger, Passenger::new);
	}

	public Passenger updatePassenger(Passenger passenger) {
		Passenger storedPassenger = find(passenger, Passenger::new);
		updatePassenger(passenger, storedPassenger);
		return storedPassenger;
	}

	private void updatePassenger(Passenger passenger, Passenger storedPassenger) {
		storedPassenger.updatePassengerData(passenger);
		if (passenger.getCreditCard() != null) {
			storedPassenger.updateCreditCard(passenger.getCreditCard());
		}
		if (passenger.getAddress() != null) {
			storedPassenger.updateAddress(passenger.getAddress());
		}
		save(storedPassenger);
	}

	public <T extends AggregateRoot> void save(T aggregate) {
		kafkaStore.save(aggregate);
	}

	public <T extends AggregateRoot> T find(T aggregate, Function<JsonObject, T> factory) {
		return kafkaStore.findByIdAndVersionWaitForResult(aggregate, json -> factory.apply(json));
	}
}