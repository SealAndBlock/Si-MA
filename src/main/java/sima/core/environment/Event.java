package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.Protocol;

/**
 * Represents an event which can occur during the simulation.
 * <p>
 * An event has a sender {@link AbstractAgent} which is the agent which as send (trigger) the event. It also has a
 * receiver which is the agent which will receive the event.
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
     * The class of the protocol which will process the event. An event can have a null instance of this property. In
     * that way the agent receive the event and chose itself how to manage the event. An event which has not protocol
     * targeted is called <i>general event</i>.
     *
     * @see GeneralEvent
     */
    private final Class<? extends Protocol> protocolTargeted;

    // Constructors.

    /**
     * Constructs an Event with the agent which send the event, the agent which will received the event, and the class
     * of the protocol which must process the event. Only the receiver must be not null.
     *
     * @param sender           the agent sender
     * @param receiver         the agent receiver
     * @param protocolTargeted the protocol targeted
     * @see Protocol#processEvent(Event)
     *
     * @throws NullPointerException if the agent receiver is null
     */
    public Event(AbstractAgent sender, AbstractAgent receiver, Class<? extends Protocol> protocolTargeted) {
        this.sender = sender;

        this.receiver = receiver;
        if (this.receiver == null)
            throw new NullPointerException("The agent receiver cannot be null");

        this.protocolTargeted = protocolTargeted;
    }

    /**
     * Returns true if the event is a <i>general event</i>, else false. An event is a general event if it has not a
     * protocol targeted.
     *
     * @return true if the protocol targeted is not null, else false.
     * @see GeneralEvent
     */
    public boolean isGeneralEvent() {
        return this.protocolTargeted != null;
    }

    // Getters and Setters.

    public AbstractAgent getSender() {
        return sender;
    }

    public AbstractAgent getReceiver() {
        return receiver;
    }

    public Class<? extends Protocol> getProtocolTargeted() {
        return protocolTargeted;
    }
}
