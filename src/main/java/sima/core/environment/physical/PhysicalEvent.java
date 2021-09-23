package sima.core.environment.physical;

import sima.core.environment.event.Event;
import sima.core.environment.event.transport.EventTransportable;

public abstract class PhysicalEvent extends Event {
    
    // Constructors.
    
    protected PhysicalEvent(EventTransportable content) {
        super(content);
    }
}
