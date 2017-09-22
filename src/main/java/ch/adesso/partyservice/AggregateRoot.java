package ch.adesso.partyservice;

import java.util.Collection;
import java.util.Collections;

import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import avro.shaded.com.google.common.collect.Lists;
import ch.adesso.partyservice.CoreEvent;
import lombok.Data;

@Data
public abstract class AggregateRoot {

	@Nullable
	private String id;

	@AvroDefault("0")
	private long version = 0;

	@JsonIgnore
	@AvroIgnore
	private Collection<CoreEvent> uncommitedEvents = Lists.newArrayList();

	public abstract void applyEvent(final CoreEvent event);

	protected void applyChange(CoreEvent event) {
		applyEvent(event);
		synchronized (uncommitedEvents) {
			uncommitedEvents.add(event);
		}
	}

	public Collection<CoreEvent> getUncommitedEvents() {
		return Collections.unmodifiableCollection(uncommitedEvents);
	}

	public void clearEvents() {
		uncommitedEvents.clear();
	}

	protected boolean wasChanged(Object o1, Object o2) {
		return o1 == null ? o2 != null : !o1.equals(o2);
	}

	protected synchronized long getNextVersion() {
		return ++version;
	}
}
