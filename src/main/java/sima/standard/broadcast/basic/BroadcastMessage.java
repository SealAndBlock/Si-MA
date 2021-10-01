package sima.standard.broadcast.basic;

import org.jetbrains.annotations.NotNull;
import sima.standard.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Objects;
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
    public BroadcastMessage(AgentIdentifier sender, Message content, ProtocolIdentifier intendedProtocol) {
        super(content, intendedProtocol);
        this.sender = Optional.of(sender).get();
    }
    
    private BroadcastMessage(BroadcastMessage other) {
        this(other.sender, other.getContent().duplicate(), other.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BroadcastMessage that)) return false;
        if (!super.equals(o)) return false;
        return getSender().equals(that.getSender());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSender());
    }
    
    @Override
    public @NotNull BroadcastMessage duplicate() {
        return new BroadcastMessage(this);
    }
    
    // Getters.
    
    public AgentIdentifier getSender() {
        return sender;
    }
}
