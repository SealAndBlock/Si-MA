package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.Protocol;

/**
 * Represents an event which can occur during the simulation.
 * <p>
 * An event has a send {@link AbstractAgent} which is the agent which as send (trigger) the event. It also has receiver
 * which is the agent which will receive the event.
 * <p>
 * In plus, the event as a protocol in attribute which is the protocol which will process the event with the method
 * {@link Protocol#processEvent(Event)}.
 */
public class Event {

    // Variables.

    /**
     * The agent sender of the event.
     */
    private final AbstractAgent sender;

    /**
     * The agent receiver of the event.
     */
    private final AbstractAgent receiver;

    /**
     * The protocol which will process the event.
     */
    private final Protocol protocolTargeted;

    // Constructors.

    public Event(AbstractAgent sender, AbstractAgent receiver, Protocol protocolTargeted) {
        this.sender = sender;
        this.receiver = receiver;
        this.protocolTargeted = protocolTargeted;
    }

    // Getters and Setters.

    public AbstractAgent getSender() {
        return sender;
    }

    public AbstractAgent getReceiver() {
        return receiver;
    }

    public Protocol getProtocolTargeted() {
        return protocolTargeted;
    }
}
