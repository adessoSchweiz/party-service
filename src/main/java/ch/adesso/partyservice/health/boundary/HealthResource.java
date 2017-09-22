package ch.adesso.partyservice.health.boundary;

import ch.adesso.partyservice.eventstore.boundary.KafkaStore;
import ch.adesso.partyservice.person.entity.Person;
import com.airhacks.porcupine.execution.boundary.Dedicated;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Path("health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class HealthResource {

    @Inject
    @Dedicated
    private ExecutorService healthPool;

    @Inject
    private KafkaStore kafkaStore;

    @GET
    public void testPassenger(
            @QueryParam("personId") String personId,
            @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> kafkaStore
                .findById(personId, Person.class), healthPool)
                .thenApply(asyncResponse::resume);
    }
}
