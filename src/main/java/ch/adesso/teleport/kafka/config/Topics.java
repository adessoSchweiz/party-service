package ch.adesso.teleport.kafka.config;

public enum Topics {

	PERSON_EVENT_TOPIC("person-events-topic"), 
	PERSON_AGGREGATE_STORE("person-aggregate-store");

	private String topic;

	Topics(String topic) {
		this.topic = topic;
	}

	public String toString() {
		return topic;
	}
}
