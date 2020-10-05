package sima.core.environment.event;

import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.UUID;

/**
 * A message is a particular event which has a content.
 */
public abstract class Message extends Event {

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
     * Constructs a {@link Message} with a sender agent, a receiver agent, a protocol targeted and a content.
     *
     * @param sender           the agent sender (cannot be null)
     * @param receiver         the agent receiver (can be null)
     * @param protocolTargeted the protocol targeted
     * @throws NullPointerException if the sender is null
     */
    public Message(UUID sender, UUID receiver, ProtocolIdentifier protocolTargeted,
                   Serializable content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Getters and Setters.

    public Serializable getContent() {
        return content;
    }
}
