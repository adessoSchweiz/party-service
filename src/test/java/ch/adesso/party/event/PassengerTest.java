package ch.adesso.party.event;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.adesso.party.entity.Passenger;

public class PassengerTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePassenger() {

		Passenger passenger = new Passenger();
		assertThat(passenger.getUncommitedEvents().size(), is(0));

		passenger = new Passenger("123");
		assertThat(passenger.getId(), is("123"));

		assertThat(passenger.getUncommitedEvents().size(), is(1));
		assertThat(passenger.getUncommitedEvents(), hasItems(instanceOf(PassengerCreatedEvent.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdatePassenger() {
		Passenger passenger = new Passenger("123");
		passenger.clearEvents();

		Passenger input = new Passenger("123");
		input.setFirstname("firstname");
		input.setLastname("lastname");

		passenger.updatePassengerData(input);

		assertThat(passenger.getUncommitedEvents().size(), is(1));
		assertThat(passenger.getUncommitedEvents(), hasItems(instanceOf(PassengerChangedEvent.class)));

	}

}
