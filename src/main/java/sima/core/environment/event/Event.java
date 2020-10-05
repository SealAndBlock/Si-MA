package sima.core.environment.event;

import sima.core.agent.AbstractAgent;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentificator;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an event which can occur during the simulation.
 * <p>
 * An event has a sender {@link AbstractAgent} which is the agent which as send (trigger) the event. It also has a
 * receiver which is the agent which will receive the event.
 * <p>
 * In plus, the event as a protocol in attribute which is the protocol which will process the event with the method
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
    private final UUID sender;

    /**
     * The agent receiver of the event.
     */
    private final UUID receiver;

    /**
     * The class of the protocol which will process the event. An event can have a null instance of this property. In
     * that way the agent receive the event and chose itself how to manage the event. An event which has not protocol
     * targeted is called <i>general event</i>.
     *
     * @see NoProtocolEvent
     */
    private final ProtocolIdentificator protocolTargeted;

    // Constructors.

    /**
     * Constructs an {@link Event} with the agent which sends the event, the agent which will receive the event, and
     * the class of the protocol which must process the event.
     * <p>
     * The sender cannot be null, if it is the case a {@link NullPointerException} is thrown.
     * <p>
     * The receiver can be null, in that case it is to the environment to manage which agent(s) are the receivers
     * of the {@code Event}.
     *
     * @param sender           the agent sender (cannot be null)
     * @param receiver         the agent receiver
     * @param protocolTargeted the protocol targeted
     * @throws NullPointerException if the sender is null
     * @see Protocol#processEvent(Event)
     */
    public Event(UUID sender, UUID receiver, ProtocolIdentificator protocolTargeted) {
        this.sender = sender;
        if (this.sender == null)
            throw new NullPointerException("The sender cannot be null");

        this.receiver = receiver;

        this.protocolTargeted = protocolTargeted;
    }

    /**
     * Returns true if the event is a {@code NoProtocolEvent}, else false. An event is a {@code NoProtocolEvent} if it
     * has not a protocol targeted.
     *
     * @return true if the protocol targeted is not null, else false.
     * @see NoProtocolEvent
     */
    public boolean isNoProtocolEvent() {
        return this.protocolTargeted != null;
    }

    // Getters and Setters.

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public ProtocolIdentificator getProtocolTargeted() {
        return protocolTargeted;
    }
}
