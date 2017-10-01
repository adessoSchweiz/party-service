package ch.adesso.party;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class NullAwareJsonObjectBuilder implements JsonObjectBuilder {

    public static JsonObjectBuilder wrap(JsonObjectBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Can't wrap nothing.");
        }
        return new NullAwareJsonObjectBuilder(builder);
    }

    private final JsonObjectBuilder builder;

    private NullAwareJsonObjectBuilder(JsonObjectBuilder builder) {
        this.builder = builder;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonValue value) {
        builder.add(name, (value == null) ? JsonValue.NULL : value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        if (value == null) {
            builder.add(name, JsonValue.NULL);
        } else {
            builder.add(name, value);
        }
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        if (value == null) {
            builder.add(name, JsonValue.NULL);
        } else {
            builder.add(name, value);
        }
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        if (value == null) {
            builder.add(name, JsonValue.NULL);
        } else {
            builder.add(name, value);
        }
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        builder.addNull(name);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder value) {
        if (value == null) {
            builder.add(name, JsonValue.NULL);
        } else {
            builder.add(name, value);
        }
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder value) {
        if (value == null) {
            builder.add(name, JsonValue.NULL);
        } else {
            builder.add(name, value);
        }
        return this;
    }

    @Override
    public JsonObject build() {
        return builder.build();
    }

}