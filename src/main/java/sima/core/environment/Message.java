package sima.core.environment;

import sima.core.agent.ProtocolIdentificator;

import java.util.UUID;

/**
 * A message is a particular event which has a content.
 */
public class Message extends Event {

    // Variables.

    /**
     * The content of the message.
     */
    private final Object content;

    // Constructors.

    public Message(UUID sender, UUID receiver, ProtocolIdentificator protocolTargeted,
                   Object content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Getters and Setters.

    public Object getContent() {
        return content;
    }
}
