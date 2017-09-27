package ch.adesso.partyservice;

import ch.adesso.partyservice.party.passenger.entity.Address;
import ch.adesso.partyservice.party.passenger.entity.CreditCard;
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
        private static final String HOUSE_NUMBER = "42a";
        private static final String CITY = "Zuerich";
        private static final String COUNTRY = "Switzerland";
        private static final String ZIP = "8000";
    }

    public static final class CREDIT_CARD {
        private static final String CARD_NUMBER = "0098 8765 5432 9876";
        private static final String CARD_TYPE = "VISA";
        private static final String NAME_ON_CARD = "Robert Brem";
        private static final int VALID_TO_MONTH = 2;
        private static final int VALID_TO_YEAR = 2020;
        private static final int SECRET_NUMBER = 123;
    }

    private static String ID;

    @Test
    public void a01_shouldCreatePassenger() {
        JsonObject personToCreate = Json.createObjectBuilder()
                .add(Passenger.JSON_KEYS.FIRSTNAME.getKeyName(), FIRSTNAME)
                .add(Passenger.JSON_KEYS.LASTNAME.getKeyName(), LASTNAME)
                .add(Passenger.JSON_KEYS.BIRTHDAY.getKeyName(), BIRTHDAY)
                .add(Passenger.JSON_KEYS.STATUS.getKeyName(), STATUS)
                .add(Passenger.JSON_KEYS.MOBIL.getKeyName(), MOBIL)
                .add(Passenger.JSON_KEYS.EMAIL.getKeyName(), EMAIL)
                .add(Passenger.JSON_KEYS.ADDRESS.getKeyName(), Json.createObjectBuilder()
                        .add(Address.JSON_KEYS.STREET.getKeyName(), ADDRESS.STREET)
                        .add(Address.JSON_KEYS.HOUSE_NUMBER.getKeyName(), ADDRESS.HOUSE_NUMBER)
                        .add(Address.JSON_KEYS.CITY.getKeyName(), ADDRESS.CITY)
                        .add(Address.JSON_KEYS.COUNTRY.getKeyName(), ADDRESS.COUNTRY)
                        .add(Address.JSON_KEYS.ZIP.getKeyName(), ADDRESS.ZIP)
                )
                .add(Passenger.JSON_KEYS.CREDIT_CARD.getKeyName(), Json.createObjectBuilder()
                        .add(CreditCard.JSON_KEYS.CARD_NUMBER.getKeyName(), CREDIT_CARD.CARD_NUMBER)
                        .add(CreditCard.JSON_KEYS.CARD_TYPE.getKeyName(), CREDIT_CARD.CARD_TYPE)
                        .add(CreditCard.JSON_KEYS.NAME_ON_CARD.getKeyName(), CREDIT_CARD.NAME_ON_CARD)
                        .add(CreditCard.JSON_KEYS.VALID_TO_MONTH.getKeyName(), CREDIT_CARD.VALID_TO_MONTH)
                        .add(CreditCard.JSON_KEYS.VALID_TO_YEAR.getKeyName(), CREDIT_CARD.VALID_TO_YEAR)
                        .add(CreditCard.JSON_KEYS.SECRET_NUMBER.getKeyName(), CREDIT_CARD.SECRET_NUMBER)
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
    public void a02_shouldReturnPassengerForHealthCheck() throws InterruptedException {
        JsonObject passenger = this.healthProvider
                .target()
                .queryParam("personId", ID)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        assertThat(passenger.getString(Passenger.JSON_KEYS.FIRSTNAME.getKeyName()), is(FIRSTNAME));
        assertThat(passenger.getString(Passenger.JSON_KEYS.LASTNAME.getKeyName()), is(LASTNAME));
        assertThat(passenger.getString(Passenger.JSON_KEYS.BIRTHDAY.getKeyName()), is(BIRTHDAY));
        assertThat(passenger.getString(Passenger.JSON_KEYS.STATUS.getKeyName()), is(STATUS));
        assertThat(passenger.getString(Passenger.JSON_KEYS.MOBIL.getKeyName()), is(MOBIL));
        assertThat(passenger.getString(Passenger.JSON_KEYS.EMAIL.getKeyName()), is(EMAIL));

        JsonObject address = passenger.getJsonObject(Passenger.JSON_KEYS.ADDRESS.getKeyName());
        assertThat(address.getString(Address.JSON_KEYS.STREET.getKeyName()), is(ADDRESS.STREET));
        assertThat(address.getString(Address.JSON_KEYS.HOUSE_NUMBER.getKeyName()), is(ADDRESS.HOUSE_NUMBER));
        assertThat(address.getString(Address.JSON_KEYS.CITY.getKeyName()), is(ADDRESS.CITY));
        assertThat(address.getString(Address.JSON_KEYS.COUNTRY.getKeyName()), is(ADDRESS.COUNTRY));
        assertThat(address.getString(Address.JSON_KEYS.ZIP.getKeyName()), is(ADDRESS.ZIP));

        JsonObject creditCard = passenger.getJsonObject(Passenger.JSON_KEYS.CREDIT_CARD.getKeyName());
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.CARD_NUMBER.getKeyName()), is(CREDIT_CARD.CARD_NUMBER));
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.CARD_TYPE.getKeyName()), is(CREDIT_CARD.CARD_TYPE));
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.NAME_ON_CARD.getKeyName()), is(CREDIT_CARD.NAME_ON_CARD));
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.VALID_TO_MONTH.getKeyName()), is(CREDIT_CARD.VALID_TO_MONTH));
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.VALID_TO_YEAR.getKeyName()), is(CREDIT_CARD.VALID_TO_YEAR));
        assertThat(creditCard.getString(CreditCard.JSON_KEYS.SECRET_NUMBER.getKeyName()), is(CREDIT_CARD.SECRET_NUMBER));
    }

}
