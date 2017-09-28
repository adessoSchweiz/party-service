package ch.adesso.partyservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

@Data
public abstract class AggregateRoot {
	private static final Logger LOG = Logger.getLogger(AggregateRoot.class.getName());

	private String id;
	private long version = 0;
	private Collection<CoreEvent> uncommitedEvents = new ArrayList<CoreEvent>();

	public void applyEvent(final CoreEvent event) {
		LOG.info("event: " + event);
		try {
			setVersion(event.getSequence());
			Method applyEvent = getClass().getDeclaredMethod("applyEvent", event.getClass());
			applyEvent.setAccessible(true);
			applyEvent.invoke(this, event);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOG.log(Level.SEVERE, "applyEvent throws Exception: ", e.getCause());
			throw new RuntimeException(e);
		}
	}

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
