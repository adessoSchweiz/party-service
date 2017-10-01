package ch.adesso.party.boundary;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.ExecutorService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import com.airhacks.porcupine.execution.boundary.Dedicated;

import ch.adesso.party.entity.Passenger;
import ch.adesso.party.kafka.KafkaStore;

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
	public void testPassenger(@QueryParam("personId") String personId, @Suspended final AsyncResponse asyncResponse) {
		supplyAsync(() -> kafkaStore.findById(personId, Passenger::new).toJson(), healthPool)
				.thenApply(asyncResponse::resume);
	}
}
