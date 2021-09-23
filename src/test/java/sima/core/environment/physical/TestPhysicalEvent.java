package sima.core.environment.physical;

import org.junit.jupiter.api.BeforeEach;
import sima.core.environment.event.TestEvent;

public abstract class TestPhysicalEvent extends TestEvent {
    
    // Variables.
    
    protected PhysicalEvent physicalEvent;
    
    // Init.
    
    @BeforeEach
    public void setUp() {
        event = physicalEvent;
    }
    
}
