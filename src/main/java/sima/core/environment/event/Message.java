package sima.core.environment.event;

import sima.core.protocol.ProtocolIdentificator;

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
     * <p>
     * The sender cannot be null, if it is the case a {@link NullPointerException} is thrown.
     * <p>
     * The receiver can be null, in that case it means that the message is destined to all agent in the environment.
     * It is a broadcast message. (See the class {@link BroadcastMessage}
     *
     * @param sender           the agent sender (cannot be null)
     * @param receiver         the agent receiver (can be null)
     * @param protocolTargeted the protocol targeted
     * @throws NullPointerException if the sender is null
     * @see BroadcastMessage
     */
    public Message(UUID sender, UUID receiver, ProtocolIdentificator protocolTargeted,
                   Serializable content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Getters and Setters.

    public Serializable getContent() {
        return content;
    }
}
