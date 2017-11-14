package ch.adesso.teleport.persons.boundary;

import static ch.adesso.teleport.JsonConverter.fromInputStream;
import static ch.adesso.teleport.JsonConverter.toJson;

import java.io.InputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.adesso.teleport.persons.entity.Person;

@Path("persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PersonResource {

	@Inject
	private PersonService personService;

	@POST
	public Response createPerson(InputStream person) {
		return Response.status(Response.Status.CREATED)
				.entity(toJson(personService.createPerson(fromInputStream(person, Person.class)))).build();
	}

	@GET
	public Response getPerson(InputStream person) {
		return Response.status(Response.Status.CREATED)
				.entity(toJson(personService.createPerson(fromInputStream(person, Person.class)))).build();
	}

	@Path("/{personId}")
	@GET
	public Response getPerson(@PathParam("personId") String personId) {
		return Response.ok().entity(toJson(personService.findPersonById(personId))).build();
	}
}
