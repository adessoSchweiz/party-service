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

import ch.adesso.partyservice.party.controller.PartyService;
import ch.adesso.partyservice.party.entity.Person;

@Path("parties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PartyResource {

	@Inject
	@Dedicated
	private ExecutorService executorService;
	
	@Inject
	private PartyService partyService;
	
	@GET()
	@Path("/test/{personId}")
	public void testPerson(@PathParam("personId") String personId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> partyService.createDummyPerson(personId))
		.thenApply(asyncResponse::resume);
	}

	@GET()
	@Path("/{personId}")
	public void getPerson(@PathParam("personId") String personId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> partyService.getPerson(personId))
		.thenApply(asyncResponse::resume);

	}

	@PUT()
	@Path("/{personId}")
	public void updatePerson(@PathParam("personId") String personId, Person person, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> partyService.updatePerson(personId, person))
		.thenApply(asyncResponse::resume);

	}
	
	@POST()
	public void createPerson(Person person, @Suspended final AsyncResponse asyncResponse)  {
		supplyAsync(() -> partyService.createPerson(person))
		.thenApply(asyncResponse::resume);

	}
}
