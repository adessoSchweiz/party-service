package ch.adesso.partyservice.party.boundary;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.ExecutorService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import com.airhacks.porcupine.execution.boundary.Dedicated;

import ch.adesso.partyservice.party.controller.PassengerService;
import ch.adesso.partyservice.party.entity.Passenger;

@Path("party")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PartyResource {

	@Inject
	@Dedicated
	private ExecutorService executorService;
	
	@Inject
	private PassengerService passengerService;
	
	@GET()
	@Path("/test/persons/{passengerId}")
	public void testPerson(@PathParam("passengerId") String passengerId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createDummyPassenger(passengerId))
		.thenApply(asyncResponse::resume);
	}

	@GET()
	@Path("/persons/{passengerId}")
	public void getPerson(@PathParam("passengerId") String passengerId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.getPassenger(passengerId))
		.thenApply(asyncResponse::resume);

	}

	@PUT()
	@Path("/persons/{passengerId}")
	public void updatePerson(@PathParam("passengerId") String passengerId, Passenger passenger, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.updatePassenger(passengerId, passenger))
		.thenApply(asyncResponse::resume);

	}
	
	@POST()
	@Path("/persons")
	public void createPerson(Passenger passenger, @Suspended final AsyncResponse asyncResponse)  {
		supplyAsync(() -> passengerService.createPassenger(passenger))
		.thenApply(asyncResponse::resume);

	}
}
