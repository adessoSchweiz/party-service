package ch.adesso.party.boundary;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.ExecutorService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.airhacks.porcupine.execution.boundary.Dedicated;

import ch.adesso.party.entity.Passenger;

@Path("passengers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PassengerResource {

	@Inject
	@Dedicated
	private ExecutorService passengerPool;

	@Inject
	private PassengerService passengerService;

	@POST
	public void createPassenger(JsonObject passenger, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(
				() -> Response.status(Response.Status.CREATED)
						.entity(passengerService.createPassenger(new Passenger(passenger)).toJson()).build(),
				passengerPool).thenApply(asyncResponse::resume);
	}

	@PUT
	public void updatePassenger(JsonObject passenger, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.updatePassenger(new Passenger(passenger)).toJson(), passengerPool)
				.thenApply(asyncResponse::resume);
	}
}
