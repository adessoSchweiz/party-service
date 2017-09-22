package ch.adesso.partyservice;

import ch.adesso.partyservice.party.person.Person;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PassengerServiceIT {
    public static final String BASE_PATH = System.getenv("BASE_PATH");

    @Rule
    public JAXRSClientProvider healthProvider = buildWithURI(BASE_PATH + "/health");

    @Rule
    public JAXRSClientProvider personProvider = buildWithURI(BASE_PATH + "/passengers");

    private static final String FIRSTNAME = "robert";
    private static final String LASTNAME = "brem";
    private static final String BIRTHDAY = "12-12-2000";
    private static String ID;

    @Test
    public void a01_shouldCreatePerson() {
        JsonObject personToCreate = Json.createObjectBuilder()
                .add(Person.JSON_KEYS.FIRSTNAME, FIRSTNAME)
                .add(Person.JSON_KEYS.LASTNAME, LASTNAME)
                .add(Person.JSON_KEYS.BIRTHDAY, BIRTHDAY)
                .build();

        Response postResponse = this.personProvider
                .target()
                .request()
                .post(Entity.json(personToCreate));

        assertThat(postResponse.getStatus(), is(201));

        ID = postResponse
                .readEntity(JsonObject.class)
                .getString(Person.JSON_KEYS.ID);
    }

    @Test
    public void a02_shouldReturnPersonForHealthCheck() throws InterruptedException {
        JsonObject person = this.healthProvider
                .target()
                .queryParam("personId", ID)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        assertThat(person.getString(Person.JSON_KEYS.FIRSTNAME), is(FIRSTNAME));
        assertThat(person.getString(Person.JSON_KEYS.LASTNAME), is(LASTNAME));
        assertThat(person.getString(Person.JSON_KEYS.BIRTHDAY), is(BIRTHDAY));
    }


}
