package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.Protocol;

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

    public Message(AbstractAgent sender, AbstractAgent receiver, Class<? extends Protocol> protocolTargeted,
                   Object content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Getters and Setters.

    public Object getContent() {
        return content;
    }
}
