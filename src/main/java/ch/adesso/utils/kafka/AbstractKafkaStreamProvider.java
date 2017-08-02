package ch.adesso.utils.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;


public abstract class AbstractKafkaStreamProvider {

	protected KafkaStreams kafkaStreams;

	public void init() {
		this.kafkaStreams = createKafkaStreams();
	}

	public void close() {
		this.kafkaStreams.close();
	}

	public KafkaStreams getKafkaStreams() {
		return kafkaStreams;
	}

	public KafkaStreams createKafkaStreams() {

		Properties props = loadProperties();
		
		KafkaStreams streams = new KafkaStreams(createStreamBuilder(), new StreamsConfig(props));

		streams.cleanUp();
		streams.start();

		return streams;
	}

	public Properties loadProperties() {
		Properties properties = new Properties();
		final InputStream stream = KafkaProducerProvider.class.getResourceAsStream("/kafka-streams.properties");
		if (stream == null) {
			throw new RuntimeException("No kafka producer properties found !!!");
		}
		try {
			properties.load(stream);
		} catch (final IOException e) {
			throw new RuntimeException("Configuration could not be loaded!");
		}

		return updateProperties(properties);
	}

	protected abstract KStreamBuilder createStreamBuilder();
	
	protected Properties updateProperties(Properties properties) {
		return properties;
	}
}
