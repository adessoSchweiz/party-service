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
    private String houseNo;
    @Nullable
    private String zip;
    @Nullable
    private String city;
    @Nullable
    private String country;

    public enum JSON_KEYS {
        STREET("street"), HOUSE_NO("house_no"), CITY("city"), COUNTRY("country"), ZIP("zip");

        @Getter
        private String name;

        JSON_KEYS(String name) {
            this.name = name;
        }

    }

    public Address(JsonObject address) {
        setStreet(address.getString(JSON_KEYS.STREET.getName()));
        setHouseNo(address.getString(JSON_KEYS.HOUSE_NO.getName()));
        setCity(address.getString(JSON_KEYS.CITY.getName()));
        setCountry(address.getString(JSON_KEYS.COUNTRY.getName()));
        setZip(address.getString(JSON_KEYS.ZIP.getName()));
    }

    public Address(Address address) {
        setStreet(address.getStreet());
        setHouseNo(address.getHouseNo());
        setCity(address.getCity());
        setCountry(address.getCountry());
        setZip(address.getZip());
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.STREET.getName(), getStreet())
                .add(JSON_KEYS.HOUSE_NO.getName(), getHouseNo())
                .add(JSON_KEYS.CITY.getName(), getCity())
                .add(JSON_KEYS.COUNTRY.getName(), getCountry())
                .add(JSON_KEYS.ZIP.getName(), getZip())
                .build();
    }

}
