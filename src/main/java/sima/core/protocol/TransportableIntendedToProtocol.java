package sima.core.protocol;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.event.transport.TransportableInEvent;

public interface TransportableIntendedToProtocol extends TransportableInEvent, IntendedToProtocol {
    
    @Override
    @NotNull TransportableIntendedToProtocol duplicate();
    
}
