package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
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
     * Because a {@link Message} is an {@link Event} and that an {@link Event} is {@link Transportable}, the content of
     * the message must be {@link Serializable}.
     */
    private final Transportable content;
    
    // Constructors.
    
    /**
     * Constructs a {@link Message} with a sender sima.core.agent, a receiver sima.core.agent, a sima.core.protocol
     * targeted and a content.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver (can be null)
     * @param protocolTargeted the sima.core.protocol targeted
     *
     * @throws NullPointerException if the sender is null
     */
    public Message(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted,
                   Transportable content) {
        super(sender, receiver, protocolTargeted);
        this.content = content;
    }
    
    private Message(Message message) {
        this(message.getSender(), message.getReceiver(), message.getProtocolTargeted(),
                message.content != null ? message.content.duplicate() : null);
    }
    
    private Message(Message message, AgentIdentifier newReceiver) {
        this(message.getSender(), newReceiver, message.getProtocolTargeted(),
                message.content != null ? message.content.duplicate() : null);
    }
    
    // Methods.
    
    @Override
    public @NotNull Message duplicate() {
        return new Message(this);
    }
    
    @Override
    public @NotNull Event duplicateWithNewReceiver(AgentIdentifier newReceiver) {
        return new Message(this, newReceiver);
    }
// Getters and Setters.
    
    @Override
    public Transportable getContent() {
        return content;
    }
}
