package ch.adesso.partyservice.party.kafka;

import java.util.Collections;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import ch.adesso.partyservice.party.entity.PartyEventStream;
import ch.adesso.partyservice.party.event.EventEnvelope;
import ch.adesso.utils.kafka.AbstractKafkaStreamProvider;
import ch.adesso.utils.kafka.KafkaAvroReflectDeserializer;
import ch.adesso.utils.kafka.KafkaAvroReflectSerializer;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;

@Startup
@Singleton
public class KafkaStreamProvider extends AbstractKafkaStreamProvider {

	private String SCHEMA_REGISTRY_URL;

	@Override
	protected KStreamBuilder createStreamBuilder() {

		final Serde<EventEnvelope> eventSerde = Serdes.serdeFrom(new KafkaAvroReflectSerializer<>(),
				new KafkaAvroReflectDeserializer<>(EventEnvelope.class));

		// important to configure schema registry
		eventSerde.configure(
				Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL),
				false);

		final Serde<PartyEventStream> eventStreamSerde = Serdes.serdeFrom(new KafkaAvroReflectSerializer<>(),
				new KafkaAvroReflectDeserializer<>(PartyEventStream.class));

		eventStreamSerde.configure(
				Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL),
				false);

		final KStreamBuilder builder = new KStreamBuilder();

		// local store for party aggregate
		StateStoreSupplier<?> stateStore = Stores.create(Topics.PARTY_STORE.getTopic()).withKeys(Serdes.String())
				.withValues(eventStreamSerde).persistent().enableCaching().build();

		StateStoreSupplier<?> stateStoreLogin = Stores.create(Topics.PARTY_LOGIN_STORE.getTopic())
				.withKeys(Serdes.String()).withValues(eventStreamSerde).persistent().enableCaching().build();

		builder.addStateStore(stateStore);
		builder.addStateStore(stateStoreLogin);

		builder.addSource("party-events-source", new StringDeserializer(), eventSerde.deserializer(),
				Topics.PARTY_EVENTS_TOPIC.getTopic())
				// aggregate event to party
				.addProcessor("party-processor",
						() -> new AggregateProcessor(Topics.PARTY_STORE.getTopic(),
								Topics.PARTY_LOGIN_STORE.getTopic()),
						"party-events-source")
				// use local store
				.connectProcessorAndStateStores("party-processor", stateStore.name(), stateStoreLogin.name());

		return builder;
	}

	protected Properties updateProperties(Properties properties) {
		SCHEMA_REGISTRY_URL = properties.getProperty("schema.registry.url");
		return super.updateProperties(properties);
	}

	@PostConstruct
	public void init() {
		super.init();
	}

	@PreDestroy
	public void close() {
		super.close();
	}

	@Produces
	public KafkaStreams getKafkaStreams() {
		return super.getKafkaStreams();
	}

}
