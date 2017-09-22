package ch.adesso.partyservice;

public enum Topics {

	PARTY_EVENTS_TOPIC("party-events-v1"),
	PARTY_TOPIC("party-v1"),
	PARTY_STORE("party-store"),
	PARTY_LOGIN_STORE("party-login-store");
	
	
	private String topic;
	
	Topics(String topic) {
		this.topic = topic;
	}
	
	public String getTopic() {
		return topic;
	}
}
