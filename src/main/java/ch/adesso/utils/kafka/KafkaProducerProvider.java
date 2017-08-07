package ch.adesso.utils.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;

import org.apache.kafka.clients.producer.KafkaProducer;

@Startup
@Singleton
public class KafkaProducerProvider {

	private KafkaProducer<String, Object> producer;

	@PostConstruct
	public void init() {
		this.producer = createProducer();
	}

	@Produces
	public KafkaProducer<String, Object> getProducer() {
		return producer;
	}

	public KafkaProducer<String, Object> createProducer() {
		return new KafkaProducer<>(loadProperties());
	}
	
	public Properties loadProperties() {
		Properties properties = new Properties();
		final InputStream stream = KafkaProducerProvider.class.getResourceAsStream("/kafka-producer.properties");
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
	protected Properties updateProperties(Properties properties) {
		return properties;
	}
}
