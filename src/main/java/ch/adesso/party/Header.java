package ch.adesso.party;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.reflect.Nullable;

public class Header {
    @Nullable
    private Map<String, Object> properties;

    public Header() {
        properties = new HashMap<>();
    }

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }
}
