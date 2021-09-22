package sima.basic.environment.message.event;

import org.jetbrains.annotations.NotNull;
import sima.basic.environment.message.Message;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.event.ProtocolEvent;

public class MessageReceptionEvent extends ProtocolEvent {
    
    // Constructors.
    
    /**
     * Constructs an {@link ProtocolEvent} which can only contains Message and which represents the reception of a {@link Message}.
     *
     * @param message the content
     */
    public MessageReceptionEvent(Message message, ProtocolIdentifier intendedProtocol) {
        super(message, intendedProtocol);
    }
    
    private MessageReceptionEvent(MessageReceptionEvent other) {
        this(other.getMessage().duplicate(), other.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public @NotNull MessageReceptionEvent duplicate() {
        return new MessageReceptionEvent(this);
    }
    
    /**
     * @return the {@link #getContent()} cast in {@link Message}.
     */
    public Message getMessage() {
        return (Message) getContent();
    }
}
