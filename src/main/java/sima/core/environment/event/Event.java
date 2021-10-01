package sima.core.environment.event;

import sima.core.agent.SimaAgent;
import sima.core.utils.Box;
import sima.core.utils.Duplicable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an event which can occur during the simulation on an {@link SimaAgent}.
 * <p>
 * <p>
 * An {@link Event} represent just an event that an {@link sima.core.environment.Environment} can create and associate to an {@link SimaAgent} which
 * will call the method {@link SimaAgent#processEvent(Event)} of the agent.
 * <p>
 * An {@link Event} can contains another {@link Event} which can be null and is just the content that the {@link Event} and which would be necessary
 * for the treatment of the {@link Event}.
 * <p>
 * An Event is {@link Serializable}. Therefore, all subclasses must have attribute {@link Serializable} attributes or using the key word
 * <i>transient</i>.
 */
public abstract class Event implements Serializable, Duplicable<Event>, Box<Event> {

    // Variables.

    private final Event content;

    // Constructors.

    /**
     * Constructs an {@link Event}. The {@link Event} can have content or not.
     */
    protected Event(Event content) {
        this.content = content;
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(getContent(), event.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent());
    }

    // Getters and Setters.

    @Override
    public Event getContent() {
        return content;
    }
}
