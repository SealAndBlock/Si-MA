package sima.core.environment;

import sima.core.agent.ProtocolIdentificator;

import java.io.Serializable;
import java.util.UUID;

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
