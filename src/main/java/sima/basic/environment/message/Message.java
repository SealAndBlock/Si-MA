package sima.basic.environment.message;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.TransportableInEvent;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.TransportableIntendedToProtocol;
import sima.core.utils.Box;

import java.io.Serializable;
import java.util.Optional;

/**
 * A message is a particular event which has a content and which is intended to a specific protocol
 */
public class Message implements TransportableIntendedToProtocol, Box<TransportableInEvent> {
    
    // Variables.
    
    /**
     * The content of the message.
     * <p>
     * Because a {@link Message} is an {@link Event} and that an {@link Event} is {@link TransportableInEvent}, the content of the message must
     * be {@link Serializable}.
     */
    private final TransportableInEvent content;
    
    /**
     * The intended protocol.
     * <p>
     * It is the protocol which must treat the message.
     */
    private final ProtocolIdentifier intendedProtocol;
    
    // Constructors.
    
    /**
     * @param content          the content
     * @param intendedProtocol the intended protocol
     *
     * @throws NullPointerException if intendedProtocol is null
     */
    public Message(TransportableInEvent content, ProtocolIdentifier intendedProtocol) {
        this.content = content;
        this.intendedProtocol = Optional.of(intendedProtocol).get();
    }
    
    private Message(Message message) {
        this(message.content.duplicate(), message.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public @NotNull Message duplicate() {
        return new Message(this);
    }
    
    // Getters and Setters.
    
    @Override
    public TransportableInEvent getContent() {
        return content;
    }
    
    @Override
    public @NotNull ProtocolIdentifier getIntendedProtocol() {
        return intendedProtocol;
    }
}
