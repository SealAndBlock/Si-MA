package sima.basic.environment.message;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.Transportable;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.utils.Box;

import java.io.Serializable;

/**
 * A message is a particular event which has a content.
 */
public class Message extends Event implements Box<Transportable> {
    
    // Variables.
    
    /**
     * The content of the message.
     * <p>
     * Because a {@link Message} is an {@link Event} and that an {@link Event} is {@link Transportable}, the content of the message must be
     * {@link Serializable}.
     */
    private final Transportable content;
    
    // Constructors.
    
    /**
     * Constructs a {@link Message} with a sender sima.core.agent, a receiver sima.core.agent, a sima.core.protocol targeted and a content.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver (can be null)
     * @param protocolTargeted the sima.core.protocol targeted
     *
     * @throws NullPointerException if the sender is null
     */
    public Message(AgentIdentifier sender, AgentIdentifier receiver, Transportable content, ProtocolIdentifier protocolTargeted) {
        super(sender, receiver, protocolTargeted);
        this.content = content;
    }
    
    private Message(Message message) {
        this(message.getSender(), message.getReceiver(), message.content != null ? message.content.duplicate() : null,
                message.getProtocolIntended());
    }
    
    private Message(Message message, AgentIdentifier newReceiver) {
        this(message.getSender(), newReceiver, message.content != null ? message.content.duplicate() : null, message.getProtocolIntended());
    }
    
    // Methods.
    
    @Override
    public @NotNull Message duplicate() {
        return new Message(this);
    }
    
    @Override
    public @NotNull Message duplicateWithNewReceiver(AgentIdentifier newReceiver) {
        return new Message(this, newReceiver);
    }
    
    // Getters and Setters.
    
    @Override
    public Transportable getContent() {
        return content;
    }
}
