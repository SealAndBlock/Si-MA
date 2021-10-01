package sima.standard.environment.message.event.physical;

import org.jetbrains.annotations.NotNull;
import sima.standard.environment.message.Message;
import sima.core.environment.event.Event;
import sima.core.environment.physical.PhysicalEvent;
import sima.core.protocol.IntendedToProtocol;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Optional;

public class PhysicalMessageReceptionEvent extends PhysicalEvent implements IntendedToProtocol {
    
    // Variables.
    
    private final ProtocolIdentifier intendedProtocol;
    
    // Constructors.
    
    public PhysicalMessageReceptionEvent(Message content, ProtocolIdentifier intendedProtocol) {
        super(content);
        this.intendedProtocol = Optional.of(intendedProtocol).get();
    }
    
    private PhysicalMessageReceptionEvent(PhysicalMessageReceptionEvent other) {
        this(other.getContent().duplicate(), other.getIntendedProtocol());
    }
    
    // Methods.
    
    @Override
    public Message getContent() {
        return (Message) super.getContent();
    }
    
    @Override
    public @NotNull Event duplicate() {
        return new PhysicalMessageReceptionEvent(this);
    }
    
    @Override
    public @NotNull ProtocolIdentifier getIntendedProtocol() {
        return intendedProtocol;
    }
}
