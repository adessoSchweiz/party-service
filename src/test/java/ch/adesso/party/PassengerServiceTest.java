package ch.adesso.party;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.adesso.party.boundary.PassengerService;
import ch.adesso.party.entity.Passenger;
import ch.adesso.party.event.PassengerChangedEvent;
import ch.adesso.party.event.PassengerCreatedEvent;
import ch.adesso.party.kafka.KafkaStore;

@RunWith(MockitoJUnitRunner.class)
public class PassengerServiceTest {

	@Mock
	private KafkaStore kafkaStore;

	@InjectMocks
	private PassengerService passengerService;

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePassenger() {

		when(kafkaStore.findByIdAndVersionWaitForResult(any(Passenger.class), any()))
				.then(AdditionalAnswers.returnsFirstArg());

		Passenger passenger = new Passenger();
		passenger.setId("111");
		passenger.setFirstname("firstname");
		passenger.setLastname("lastname");

		Passenger p = passengerService.createPassenger(passenger);
		assertThat(p.getId(), notNullValue());

		assertThat(p.getUncommitedEvents().size(), is(2));
		assertThat(p.getUncommitedEvents(),
				hasItems(instanceOf(PassengerCreatedEvent.class), instanceOf(PassengerChangedEvent.class)));
	}

}
