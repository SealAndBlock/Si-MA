package sima.core.environment.event;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents an event which can occur during the simulation.
 * <p>
 * An event has a sender {@link AbstractAgent} which is the sima.core.agent which as send (trigger) the event. It also has a
 * receiver which is the sima.core.agent which will receive the event.
 * <p>
 * In plus, the event as a sima.core.protocol in attribute which is the sima.core.protocol which will process the event with the method
 * {@link Protocol#processEvent(Event)}.
 * <p>
 * An Event is {@link Serializable}. Therefore all sub classes must have attribute {@link Serializable} attributes or
 * using the key word <i>transient</i>.
 */
public abstract class Event implements Serializable {

    // Variables.

    /**
     * The agent sender of the event.
     */
    private final AgentIdentifier sender;

    /**
     * The agent receiver of the event.
     */
    private AgentIdentifier receiver;

    /**
     * The class of the sima.core.protocol which will process the event. An event can have a null instance of this property. In
     * that way the sima.core.agent receive the event and chose itself how to manage the event. An event which has not sima.core.protocol
     * targeted is called <i>general event</i>.
     */
    private final ProtocolIdentifier protocolTargeted;

    // Constructors.

    /**
     * Constructs an {@link Event} with the sima.core.agent which sends the event, the sima.core.agent which will receive the event, and
     * the class of the sima.core.protocol which must process the event.
     * <p>
     * The sender cannot be null, if it is the case a {@link NullPointerException} is thrown.
     * <p>
     * The receiver can be null, in that case it is to the sima.core.environment to manage which sima.core.agent(s) are the receivers
     * of the {@code Event}.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver
     * @param protocolTargeted the sima.core.protocol targeted
     * @throws NullPointerException if the sender is null
     * @see Protocol#processEvent(Event)
     */
    public Event(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted) {
        this.sender = Optional.of(sender).get();
        this.receiver = receiver;
        this.protocolTargeted = protocolTargeted;
    }

    /**
     * Create a clone of the event and set to this clone the specified receiver. This methods is useful for environment
     * which must manage event with null receiver. Indeed, it is not possible to schedule an event with no receiver.
     *
     * @param receiver the receiver of the event
     * @return a new instance of the event but with a new receiver.
     */
    public final Event cloneAndAddReceiver(AgentIdentifier receiver) {
        try {
            Event newInstance = (Event) clone();
            newInstance.receiver = receiver;
            return newInstance;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Returns true if the event is a {@code ProtocolEvent}, else false. An event is a {@code ProtocolEvent} if it
     * has a sima.core.protocol targeted not null.
     *
     * @return true if the sima.core.protocol targeted is not null, else false.
     */
    public boolean isProtocolEvent() {
        return protocolTargeted != null;
    }

    // Getters and Setters.

    public AgentIdentifier getSender() {
        return sender;
    }

    public AgentIdentifier getReceiver() {
        return receiver;
    }

    public ProtocolIdentifier getProtocolTargeted() {
        return protocolTargeted;
    }
}
