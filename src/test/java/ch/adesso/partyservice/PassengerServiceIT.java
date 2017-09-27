package ch.adesso.partyservice;

import ch.adesso.partyservice.party.passenger.entity.Address;
import ch.adesso.partyservice.party.passenger.entity.Passenger;
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
    private static final String STATUS = "COMPLETED";
    private static final String MOBIL = "+41 798 45 23";
    private static final String EMAIL = "test@blub.com";

    public static final class ADDRESS {
        private static final String STREET = "Bahnhaldenstrasse";
        private static final String HOUSE_NO = "42";
        private static final String CITY = "Zürich";
        private static final String COUNTRY = "Switzerland";
        private static final String ZIP = "8000";
    }

    private static String ID;

    @Test
    public void a01_shouldCreatePerson() {
        JsonObject personToCreate = Json.createObjectBuilder()
                .add(Passenger.JSON_KEYS.FIRSTNAME.getKeyName(), FIRSTNAME)
                .add(Passenger.JSON_KEYS.LASTNAME.getKeyName(), LASTNAME)
                .add(Passenger.JSON_KEYS.BIRTHDAY.getKeyName(), BIRTHDAY)
                .add(Passenger.JSON_KEYS.STATUS.getKeyName(), STATUS)
                .add(Passenger.JSON_KEYS.MOBIL.getKeyName(), MOBIL)
                .add(Passenger.JSON_KEYS.EMAIL.getKeyName(), EMAIL)
                .add(Passenger.JSON_KEYS.ADDRESS.getKeyName(), Json.createObjectBuilder()
                        .add(Address.JSON_KEYS.STREET.getKeyName(), ADDRESS.STREET)
                        .add(Address.JSON_KEYS.HOUSE_NO.getKeyName(), ADDRESS.HOUSE_NO)
                        .add(Address.JSON_KEYS.CITY.getKeyName(), ADDRESS.CITY)
                        .add(Address.JSON_KEYS.COUNTRY.getKeyName(), ADDRESS.COUNTRY)
                        .add(Address.JSON_KEYS.ZIP.getKeyName(), ADDRESS.ZIP)
                )
                .build();

        Response postResponse = this.personProvider
                .target()
                .request()
                .post(Entity.json(personToCreate));

        assertThat(postResponse.getStatus(), is(201));

        ID = postResponse
                .readEntity(JsonObject.class)
                .getString(Passenger.JSON_KEYS.ID.getKeyName());
    }

    @Test
    public void a02_shouldReturnPersonForHealthCheck() throws InterruptedException {
        JsonObject person = this.healthProvider
                .target()
                .queryParam("personId", ID)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        assertThat(person.getString(Passenger.JSON_KEYS.FIRSTNAME.getKeyName()), is(FIRSTNAME));
        assertThat(person.getString(Passenger.JSON_KEYS.LASTNAME.getKeyName()), is(LASTNAME));
        assertThat(person.getString(Passenger.JSON_KEYS.BIRTHDAY.getKeyName()), is(BIRTHDAY));
        assertThat(person.getString(Passenger.JSON_KEYS.STATUS.getKeyName()), is(STATUS));
        assertThat(person.getString(Passenger.JSON_KEYS.MOBIL.getKeyName()), is(MOBIL));
        assertThat(person.getString(Passenger.JSON_KEYS.EMAIL.getKeyName()), is(EMAIL));

        JsonObject address = person.getJsonObject(Passenger.JSON_KEYS.ADDRESS.getKeyName());
        System.out.println("address = " + address);
        assertThat(address.getString(Address.JSON_KEYS.STREET.getKeyName()), is(ADDRESS.STREET));
        assertThat(address.getString(Address.JSON_KEYS.HOUSE_NO.getKeyName()), is(ADDRESS.HOUSE_NO));
        assertThat(address.getString(Address.JSON_KEYS.CITY.getKeyName()), is(ADDRESS.CITY));
        assertThat(address.getString(Address.JSON_KEYS.COUNTRY.getKeyName()), is(ADDRESS.COUNTRY));
        assertThat(address.getString(Address.JSON_KEYS.ZIP.getKeyName()), is(ADDRESS.ZIP));
    }

}
