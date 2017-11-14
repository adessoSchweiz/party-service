package ch.adesso.teleport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

public class JsonConverter {

	private static JsonbConfig cfg = new JsonbConfig().withNullValues(Boolean.FALSE).withFormatting(Boolean.TRUE)
			.withDateFormat("dd-MM-yyyy", Locale.getDefault())
			.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);

	private static Jsonb jsonb = JsonbBuilder.create(cfg);

	public static JsonbConfig getJsonbConfig() {
		return cfg;
	}

	public static String toJson(Object object) {
		return object == null ? null : jsonb.toJson(object);
	}

	public static <T> T fromInputStream(InputStream is, Class<T> clazz) {
		return is == null ? null : jsonb.fromJson(is, clazz);
	}

	public static <T> T fromByteArray(byte[] bytes, Class<T> clazz) {

		if (bytes == null) {
			return null;
		}

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			return jsonb.fromJson(is, clazz);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}
