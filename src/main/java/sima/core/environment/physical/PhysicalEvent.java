package sima.core.environment.physical;

import sima.core.environment.event.Event;
import sima.core.environment.event.transport.TransportableInEvent;

public abstract class PhysicalEvent extends Event {
    
    // Constructors.
    
    protected PhysicalEvent(TransportableInEvent content) {
        super(content);
    }
}
