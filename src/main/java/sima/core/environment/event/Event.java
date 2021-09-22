package sima.core.environment.event;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.utils.Duplicable;

import java.io.Serializable;

/**
 * Represents an event which can occur during the simulation on an {@link SimaAgent}.
 * <p>
 * <p>
 * An {@link Event} represent just an event that an {@link sima.core.environment.Environment} can create and associate to an {@link SimaAgent}
 * which will call the method {@link SimaAgent#processEvent(Event)} of the agent.
 * <p>
 * An {@link Event} contains an {@link EventTransportable} which can be null and is just the content that the {@link Event} and which must be
 * necessary for the treatment of the {@link Event}.
 * <p>
 * An Event is {@link Serializable}. Therefore, all subclasses must have attribute {@link Serializable} attributes or using the key word
 * <i>transient</i>.
 */
public abstract class Event implements Serializable, Duplicable<Event> {
    
    // Variables.
    
    private final EventTransportable content;
    
    // Constructors.
    
    /**
     * Constructs an {@link Event}. The {@link Event} can have content or not.
     */
    protected Event(EventTransportable content) {
        this.content = content;
    }
    
    // Getters and Setters.
    
    public EventTransportable getContent() {
        return content;
    }
}
