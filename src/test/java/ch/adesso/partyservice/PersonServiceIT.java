package ch.adesso.partyservice;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersonServiceIT {
    public static final String BASE_PATH = System.getenv("BASE_PATH");

    @Rule
    public JAXRSClientProvider healthProvider = buildWithURI(BASE_PATH + "/health");

    @Test
    public void shouldReturnPerson() {
        JsonObject person = this.healthProvider
                .target()
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        System.out.println("person = " + person);
        assertThat(person.getString("firstname"), is("firstname"));
    }


}
