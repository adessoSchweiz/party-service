package ch.adesso.partyservice.party.boundary;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.ExecutorService;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
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
import ch.adesso.partyservice.party.entity.Credentials;
import ch.adesso.partyservice.party.entity.Passenger;

@Path("party")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PartyResource {

	@Inject
	@Dedicated
	private ExecutorService executorService;

	@Inject
	private PassengerService passengerService;

	@GET()
	@Path("/test/persons/{passengerId}")
	public void testPassenger(@PathParam("passengerId") String passengerId,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createDummyPassenger(passengerId), executorService)
				.thenApply(asyncResponse::resume);
	}

	@GET()
	@Path("/v1/persons/{passengerId}/{version}")
	public void getPassenger(@PathParam("passengerId") String passengerId, @PathParam("version") Long version,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.getPassengerWithVersion(passengerId, version), executorService)
				.thenApply(asyncResponse::resume).exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@GET()
	@Path("/v1/persons/{passengerId}")
	public void getPassenger(@PathParam("passengerId") String passengerId,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.getPassenger(passengerId), executorService).thenApply(asyncResponse::resume)
				.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@POST()
	@Path("/v1/persons/login")
	public void getPassengerByLogin(Credentials credentials, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.getPassengerByLogin(credentials.getLogin(), credentials.getPassword()),
				executorService).thenApply(asyncResponse::resume)
						.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@POST()
	@Path("/v1/persons")
	public void createPassengerV1(Passenger passenger, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createAndReturnPassenger(passenger), executorService)
				.thenApply(asyncResponse::resume).exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@PUT()
	@Path("/v1/persons/{passengerId}")
	public void updatePassengerV1(@PathParam("passengerId") String passengerId, Passenger passenger,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.updateAndReturnPassenger(passengerId, passenger), executorService)
				.thenApply(asyncResponse::resume).exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex)));

	}

	@POST()
	@Path("/v2/persons")
	public void createPassengerV2(Passenger passenger, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createPassenger(passenger), executorService).thenApply(asyncResponse::resume)
				.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@PUT()
	@Path("/v2/persons/{passengerId}")
	public void updatePassengerV2(@PathParam("passengerId") String passengerId, Passenger passenger,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.updatePassenger(passengerId, passenger), executorService)
				.thenApply(asyncResponse::resume).exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex)));

	}

}
