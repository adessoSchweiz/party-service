package ch.adesso.partyservice.health.boundary;

import ch.adesso.partyservice.party.controller.PassengerService;
import com.airhacks.porcupine.execution.boundary.Dedicated;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Path("health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class HealthResource {

    @Inject
    @Dedicated
    private ExecutorService executorService;

    @Inject
    private PassengerService passengerService;

    @GET()
    public void testPassenger( @Suspended final AsyncResponse asyncResponse) {
        supplyAsync(() -> passengerService.createDummyPassenger(UUID.randomUUID().toString()), executorService)
                .thenApply(asyncResponse::resume);
    }
}
