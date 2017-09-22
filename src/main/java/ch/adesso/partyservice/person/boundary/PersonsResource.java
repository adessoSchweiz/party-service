package ch.adesso.partyservice.person.boundary;

import ch.adesso.partyservice.person.entity.Person;
import com.airhacks.porcupine.execution.boundary.Dedicated;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Path("persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PersonsResource {

    @Inject
    @Dedicated
    private ExecutorService personPool;

    @Inject
    private PersonService personService;

    @POST
    public void createPerson(JsonObject person, @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> personService.createPerson(new Person(person)), personPool)
                .thenApply(asyncResponse::resume);
    }

    @PUT
    public void updatePerson(JsonObject person,
                             @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> personService.updatePerson(new Person(person)), personPool)
                .thenApply(asyncResponse::resume);
    }
}
