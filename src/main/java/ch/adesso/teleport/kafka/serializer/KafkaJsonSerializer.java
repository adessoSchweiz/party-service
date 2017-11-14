package ch.adesso.teleport.kafka.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import ch.adesso.teleport.JsonConverter;

public class KafkaJsonSerializer<T> implements Serializer<T> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> config, boolean isKey) {
	}

	@Override
	public byte[] serialize(String topic, T data) {
		if (data == null) {
			return null;
		}
		return JsonConverter.toJson(data).getBytes();
	}

}
