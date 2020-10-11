package sima.core.environment.event;

import sima.core.agent.AbstractAgent;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.UUID;

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
     * The sima.core.agent sender of the event.
     */
    private final UUID sender;

    /**
     * The sima.core.agent receiver of the event.
     */
    private final UUID receiver;

    /**
     * The class of the sima.core.protocol which will process the event. An event can have a null instance of this property. In
     * that way the sima.core.agent receive the event and chose itself how to manage the event. An event which has not sima.core.protocol
     * targeted is called <i>general event</i>.
     *
     * @see NoProtocolEvent
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
    public Event(UUID sender, UUID receiver, ProtocolIdentifier protocolTargeted) {
        this.sender = sender;
        if (this.sender == null)
            throw new NullPointerException("The sender cannot be null");

        this.receiver = receiver;

        this.protocolTargeted = protocolTargeted;
    }

    /**
     * Returns true if the event is a {@code NoProtocolEvent}, else false. An event is a {@code NoProtocolEvent} if it
     * has not a sima.core.protocol targeted.
     *
     * @return true if the sima.core.protocol targeted is not null, else false.
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

    public ProtocolIdentifier getProtocolTargeted() {
        return protocolTargeted;
    }
}
