package ch.adesso.partyservice;

import avro.shaded.com.google.common.collect.Lists;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

@Data
public abstract class AggregateRoot {
    private static final Logger LOG = Logger.getLogger(AggregateRoot.class.getName());

    @Nullable
    private String id;
    @AvroDefault("0")
    private long version = 0;
    @JsonIgnore
    @AvroIgnore
    private Collection<CoreEvent> uncommitedEvents = Lists.newArrayList();

    public void applyEvent(final CoreEvent event) {
        LOG.info("event: " + event);
        try {
            Method applyEvent = getClass()
                    .getDeclaredMethod("applyEvent", event.getClass());
            applyEvent.setAccessible(true);
            applyEvent.invoke(this, event);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
