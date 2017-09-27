package ch.adesso.partyservice.party.passenger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.avro.reflect.Nullable;

import javax.json.Json;
import javax.json.JsonObject;

@AllArgsConstructor
@Data
public class Address {
    @Nullable
    private String street;
    @Nullable
    private String houseNumber;
    @Nullable
    private String zip;
    @Nullable
    private String city;
    @Nullable
    private String country;

    public enum JSON_KEYS {
        STREET("street"), HOUSE_NUMBER("house_number"), CITY("city"), COUNTRY("country"), ZIP("zip");

        @Getter
        private String keyName;

        JSON_KEYS(String keyName) {
            this.keyName = keyName;
        }

    }

    public Address(JsonObject address) {
        setStreet(address.getString(JSON_KEYS.STREET.getKeyName(), null));
        setHouseNumber(address.getString(JSON_KEYS.HOUSE_NUMBER.getKeyName(), null));
        setCity(address.getString(JSON_KEYS.CITY.getKeyName(), null));
        setCountry(address.getString(JSON_KEYS.COUNTRY.getKeyName(), null));
        setZip(address.getString(JSON_KEYS.ZIP.getKeyName(), null));
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.STREET.getKeyName(), getStreet())
                .add(JSON_KEYS.HOUSE_NUMBER.getKeyName(), this.getHouseNumber())
                .add(JSON_KEYS.CITY.getKeyName(), getCity())
                .add(JSON_KEYS.COUNTRY.getKeyName(), getCountry())
                .add(JSON_KEYS.ZIP.getKeyName(), getZip())
                .build();
    }

}
