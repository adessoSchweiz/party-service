package ch.adesso.teleport.kafka.store;

import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ch.adesso.teleport.AggregateRoot;
import kafka.common.KafkaException;

public class KafkaEventStore {

	private KafkaStreams kafkaStreams;
	private QueryableStoreUtils queryableStoreUtils = new QueryableStoreUtils();

	public KafkaEventStore(KafkaStreams kafkaStreams) {
		this.kafkaStreams = kafkaStreams;
	}

	public <T extends AggregateRoot> T findById(String storeName, String id) {
		return loadAggregateFromLocalStore(storeName, id);
	}

	public <T extends AggregateRoot> T findByIdAndVersion(String storeName, T aggregate) {
		AggregateRoot root = loadAggregateFromLocalStore(storeName, aggregate.getId());
		if (root == null || (root.getVersion() != aggregate.getVersion())) {
			return findByIdAndVersionWaitForResult(storeName, aggregate);
		}
		return (T) root;
	}

	private <T extends AggregateRoot> T findByIdAndVersionWaitForResult(String storeName, T aggregate) {
		int loop = 0;
		String id = aggregate.getId();
		long version = aggregate.getVersion();
		while (true) {
			AggregateRoot root = loadAggregateFromLocalStore(storeName, id);
			if (root == null || (root.getVersion() != version)) {
				loop++;
				if (loop > 20) {
					break;
				}
				try {
					Thread.sleep(50L);
					continue;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}

			}

			return (T) root;
		}

		return null;
	}

	public <T extends AggregateRoot> T loadAggregateFromLocalStore(String storeName, String aggregateId) {
		ReadOnlyKeyValueStore<String, T> store = null;
		try {
			store = queryableStoreUtils.waitUntilStoreIsQueryable(storeName, keyValueStore(), kafkaStreams);

		} catch (InterruptedException e) {
			throw new KafkaException("KeyValueStore can not read current data.", e);
		}

		return store.get(aggregateId);
	}

}
