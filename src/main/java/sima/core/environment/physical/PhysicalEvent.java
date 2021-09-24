package sima.core.environment.physical;

import sima.core.environment.event.Event;

public abstract class PhysicalEvent extends Event {
    
    // Constructors.
    
    protected PhysicalEvent(Event content) {
        super(content);
    }
}
