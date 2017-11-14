package ch.adesso.teleport.kafka.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

/**
 * 
 * example taken from
 * https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
 * 
 */
public class KafkaConsumerRunner<T> implements Runnable {

	private static final Logger LOG = Logger.getLogger(KafkaConsumerRunner.class.getName());

	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final KafkaConsumer<String, T> consumer;

	private List<String> topics;
	private Consumer<T> eventConsumer;

	public KafkaConsumerRunner(Properties consumerProperties, Consumer<T> eventConsumer, String... topic) {
		this.topics = Arrays.asList(topic);
		this.eventConsumer = eventConsumer;

		consumer = new KafkaConsumer<>(consumerProperties);
		consumer.subscribe(topics);
	}

	public void run() {
		try {
			while (!closed.get()) {
				ConsumerRecords<String, T> records = consumer.poll(10000);
				records.forEach(record -> eventConsumer.accept(record.value()));
				consumer.commitAsync();
			}
		} catch (WakeupException e) {
			// Ignore exception if closing
			if (!closed.get())
				throw e;
		} finally {
			consumer.close();
		}
	}

	// Shutdown hook which can be called from a separate thread
	public void shutdown() {
		closed.set(true);
		consumer.wakeup();
	}
}
