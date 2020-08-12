package sima.core.agent;

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

    public Message(AbstractAgent sender, AbstractAgent receiver, Protocol protocolTargeted, Object content) {
        super(sender, receiver, protocolTargeted);

        this.content = content;
    }

    // Getters and Setters.

    public Object getContent() {
        return content;
    }
}
