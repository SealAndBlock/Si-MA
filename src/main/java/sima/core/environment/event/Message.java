package sima.core.environment.event;

import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;

/**
 * A message is a particular event which has a content.
 */
public class Message extends Event {

    // Variables.

    /**
     * The content of the message.
     * <p>
     * Because a {@link Message} is an {@link Event} and that an {@link Event} is {@link Serializable}, the content of
     * the message must be {@link Serializable}.
     */
    private final Serializable content;

    // Constructors.

    /**
     * Constructs a {@link Message} with a sender sima.core.agent, a receiver sima.core.agent, a sima.core.protocol targeted and a content.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver (can be null)
     * @param protocolTargeted the sima.core.protocol targeted
     * @throws NullPointerException if the sender is null
     */
    public Message(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted,
                   Serializable content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Methods.

    @Override
    public Object clone() {
        return new Message(this.getSender(), this.getReceiver(), this.getProtocolTargeted(), this.getContent());
    }

    // Getters and Setters.

    public Serializable getContent() {
        return content;
    }
}
