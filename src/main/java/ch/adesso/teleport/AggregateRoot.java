package ch.adesso.teleport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import javax.json.bind.annotation.JsonbTransient;

import avro.shaded.com.google.common.collect.Lists;
import lombok.Data;

@Data
public abstract class AggregateRoot {

	private String id;

	private long version = 0;

	@JsonbTransient
	private Collection<CoreEvent> uncommitedEvents = Lists.newArrayList();

	public <E extends CoreEvent> void applyEvent(final E event) {
		setVersion(event.getSequence());
		try {
			Method m = getClass().getDeclaredMethod("on", event.getClass());
			m.setAccessible(true);
			m.invoke(this, event);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected void applyChange(CoreEvent event) {
		applyEvent(event);
		synchronized (uncommitedEvents) {
			uncommitedEvents.add(event);
		}
	}

	public Collection<? extends CoreEvent> getUncommitedEvents() {
		return Collections.unmodifiableCollection(uncommitedEvents);
	}

	public void clearEvents() {
		uncommitedEvents.clear();
	}

	protected boolean wasChanged(Object o1, Object o2) {
		return o1 == null ? o2 != null : !o1.equals(o2);
	}

	protected long getNextVersion() {
		return ++version;
	}
}
