package sima.basic.broadcast.message;

import org.jetbrains.annotations.NotNull;
import sima.basic.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.TransportableIntendedToProtocol;

import java.util.Optional;

public class BroadcastMessage extends Message {
    
    // Variables.
    
    private final AgentIdentifier sender;
    
    // Constructors.
    
    /**
     * @param sender           the message sender
     * @param content          the content
     * @param intendedProtocol the intended protocol
     *
     * @throws NullPointerException if the sender or the intended protocol is null
     */
    public BroadcastMessage(AgentIdentifier sender, TransportableIntendedToProtocol content, ProtocolIdentifier intendedProtocol) {
        super(content, intendedProtocol);
        this.sender = Optional.of(sender).get();
    }
    
    private BroadcastMessage(BroadcastMessage other) {
        this(other.sender, other.getContent().duplicate(), other.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public @NotNull BroadcastMessage duplicate() {
        return new BroadcastMessage(this);
    }
    
    @Override
    public TransportableIntendedToProtocol getContent() {
        return (TransportableIntendedToProtocol) super.getContent();
    }
    
    // Getters.
    
    public AgentIdentifier getSender() {
        return sender;
    }
}
