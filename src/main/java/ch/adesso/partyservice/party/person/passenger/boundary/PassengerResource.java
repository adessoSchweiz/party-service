package ch.adesso.partyservice.party.person.passenger.boundary;

import ch.adesso.partyservice.party.person.Person;
import com.airhacks.porcupine.execution.boundary.Dedicated;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Path("passengers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PassengerResource {

    @Inject
    @Dedicated
    private ExecutorService personPool;

    @Inject
    private PassengerService passengerService;

    @POST
    public void createPerson(JsonObject person, @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> Response
                .status(Response.Status.CREATED)
                .entity(passengerService.createPerson(new Person(person)))
                .build(), personPool)
                .thenApply(asyncResponse::resume);
    }

    @PUT
    public void updatePerson(JsonObject person,
                             @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> passengerService.updatePerson(new Person(person)), personPool)
                .thenApply(asyncResponse::resume);
    }
}
