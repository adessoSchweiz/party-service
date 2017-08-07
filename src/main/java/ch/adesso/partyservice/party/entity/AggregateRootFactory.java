package ch.adesso.partyservice.party.entity;

public interface AggregateRootFactory<T extends AggregateRoot> {
	
	public T newInstance(String aggregateId);

}
