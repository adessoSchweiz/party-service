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
import ch.adesso.partyservice.party.entity.Person;

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
	@Path("/test/persons/{personId}")
	public void testPassenger(@PathParam("personId") String personId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createDummyPassenger(personId), executorService)
				.thenApply(asyncResponse::resume);
	}

	@GET()
	@Path("/persons/{personId}/{version}")
	public void getPerson(@PathParam("personId") String personId, @PathParam("version") Long version,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.findPersonWithVersion(personId, version), executorService)
				.thenApply(asyncResponse::resume).exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@GET()
	@Path("/persons/{personId}")
	public void getPerson(@PathParam("personId") String personId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.findPersonById(personId), executorService).thenApply(asyncResponse::resume)
				.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@POST()
	@Path("/persons/login")
	public void getPersonByLogin(Credentials credentials, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.findPersonByLogin(credentials.getLogin(), credentials.getPassword()),
				executorService).thenApply(asyncResponse::resume)
						.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@POST()
	@Path("/persons")
	public void createPerson(Person person, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.createPerson(person), executorService).thenApply(asyncResponse::resume)
				.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}

	@PUT()
	@Path("/persons/{personId}")
	public void updatePerson(@PathParam("personId") String personId, Person person,
			@Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> passengerService.updatePerson(person), executorService).thenApply(asyncResponse::resume)
				.exceptionally(ex -> asyncResponse.resume(new ErrorInfo(ex).build()));

	}
}
