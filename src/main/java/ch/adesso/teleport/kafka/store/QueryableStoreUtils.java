package ch.adesso.teleport.kafka.store;

import java.util.Collection;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.StreamsMetadata;

public class QueryableStoreUtils {

	public <T> T waitUntilStoreIsQueryable(final String storeName, final QueryableStoreType<T> queryableStoreType,
			final KafkaStreams streams) throws InterruptedException {

		int loop = 0;
		while (loop < 10) {
			try {
				return streams.store(storeName, queryableStoreType);
			} catch (InvalidStateStoreException ignored) {
				Collection<StreamsMetadata> hosts = streams.allMetadataForStore(storeName);
				System.out.println("store not yet ready for querying ");
				ignored.printStackTrace();
				hosts.forEach(metaData -> System.out.println(metaData.host() + ":" + metaData.port()));

				// initialization take some time specially if brokers run on the same machine
				Thread.sleep(5000);
				loop++;
			}
		}
		return streams.store(storeName, queryableStoreType);
	}
}
