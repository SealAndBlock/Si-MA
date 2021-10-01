package sima.standard.environment.message.event;

import org.jetbrains.annotations.NotNull;
import sima.standard.environment.message.Message;
import sima.core.environment.event.Event;
import sima.core.protocol.IntendedToProtocol;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Optional;

public class MessageReceptionEvent extends Event implements IntendedToProtocol {
    
    // Variables.
    
    private final ProtocolIdentifier intendedProtocol;
    
    // Constructors.
    
    /**
     * Constructs an {@link Event} which can only contains Message and which represents the reception of a {@link Message}.
     * <p>
     * Associate an {@link sima.core.protocol.Protocol} which is the protocol which must treat the {@link Event}. It cannot be null.
     *
     * @param message          the content
     * @param intendedProtocol the intended protocol
     *
     * @throws NullPointerException if intendedProtocol is null
     */
    public MessageReceptionEvent(Message message, ProtocolIdentifier intendedProtocol) {
        super(message);
        this.intendedProtocol = Optional.of(intendedProtocol).get();
    }
    
    private MessageReceptionEvent(MessageReceptionEvent other) {
        this(other.getMessage().duplicate(), other.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public @NotNull MessageReceptionEvent duplicate() {
        return new MessageReceptionEvent(this);
    }
    
    @Override
    public @NotNull ProtocolIdentifier getIntendedProtocol() {
        return intendedProtocol;
    }
    
    /**
     * @return the {@link #getContent()} cast in {@link Message}.
     */
    public Message getMessage() {
        return (Message) getContent();
    }
}
