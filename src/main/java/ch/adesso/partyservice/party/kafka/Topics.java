package ch.adesso.partyservice.party.kafka;

public enum Topics {

	PASSENGER_EVENTS_TOPIC("passenger-events-v1"),
	PASSENGER_TOPIC("passenger-v1"),
	PASSENGER_STORE("passenger-store"),
	PASSENGER_LOGIN_STORE("passenger-login-store");
	
	
	private String topic;
	
	Topics(String topic) {
		this.topic = topic;
	}
	
	public String getTopic() {
		return topic;
	}
}
