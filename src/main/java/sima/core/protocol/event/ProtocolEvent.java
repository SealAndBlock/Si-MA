package sima.core.protocol.event;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Optional;

/**
 * An {@link Event} which is intended to a specific {@link sima.core.protocol.Protocol}.
 */
public abstract class ProtocolEvent extends Event {
    
    // Variables.
    
    private final ProtocolIdentifier intendedProtocol;
    
    // Constructors.
    
    /**
     * @param eventTransportable the content
     * @param intendedProtocol   the protocol which must process the {@link Event}
     *
     * @throws NullPointerException if the intendedProtocol is null
     */
    protected ProtocolEvent(EventTransportable eventTransportable, ProtocolIdentifier intendedProtocol) {
        super(eventTransportable);
        this.intendedProtocol = Optional.of(intendedProtocol).get();
    }
    
    // Getters.
    
    public @NotNull ProtocolIdentifier getIntendedProtocol() {
        return intendedProtocol;
    }
}
