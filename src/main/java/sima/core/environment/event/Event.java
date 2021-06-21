package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.SimpleAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents an event which can occur during the simulation.
 * <p>
 * An event has a sender {@link SimpleAgent} which is the sima.core.agent which as send (trigger) the event. It also
 * has a receiver which is the sima.core.agent which will receive the event.
 * <p>
 * In plus, the event as a sima.core.protocol in attribute which is the sima.core.protocol which will process the event
 * with the method {@link Protocol#processEvent(Event)}.
 * <p>
 * An Event is {@link Serializable}. Therefore all sub classes must have attribute {@link Serializable} attributes or
 * using the key word <i>transient</i>.
 */
public abstract class Event implements Transportable {
    
    // Variables.
    
    /**
     * The agent sender of the event.
     */
    private final AgentIdentifier sender;
    
    /**
     * The agent receiver of the event.
     */
    private final AgentIdentifier receiver;
    
    /**
     * The class of the sima.core.protocol which will process the event. An event can have a null instance of this
     * property. In that way the sima.core.agent receive the event and chose itself how to manage the event. An event
     * which has not sima.core.protocol targeted is called <i>general event</i>.
     */
    private final ProtocolIdentifier protocolTargeted;
    
    // Constructors.
    
    /**
     * Constructs an {@link Event} with the sima.core.agent which sends the event, the sima.core.agent which will
     * receive the event, and the class of the sima.core.protocol which must process the event.
     * <p>
     * The sender cannot be null, if it is the case a {@link NullPointerException} is thrown.
     * <p>
     * The receiver can be null, in that case it is to the sima.core.environment to manage which sima.core.agent(s) are
     * the receivers of the {@code Event}.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver
     * @param protocolTargeted the sima.core.protocol targeted
     *
     * @throws NullPointerException if the sender is null
     * @see Protocol#processEvent(Event)
     */
    protected Event(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted) {
        this.sender = Optional.of(sender).get();
        this.receiver = receiver;
        this.protocolTargeted = protocolTargeted;
    }
    
    // Methods.
    
    @Override
    public abstract @NotNull Event duplicate();
    
    /**
     * Duplicate the {@link Event} but change the receiver of the {@code Event}.
     *
     * @param newReceiver the receiver that the duplicated {@code Event} will have
     *
     * @return a duplicated {@code Event} with a new receiver.
     */
    public abstract @NotNull Event duplicateWithNewReceiver(AgentIdentifier newReceiver);
    
    /**
     * Returns true if the event is a {@code ProtocolEvent}, else false. An event is a {@code ProtocolEvent} if it has a
     * sima.core.protocol targeted not null.
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
