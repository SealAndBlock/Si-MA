package sima.testing.environment.physical;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.environment.physical.PhysicalEvent;

import java.util.Map;

public class SimplePhysicalConnectionLayer extends PhysicalConnectionLayer {
    
    // Constructors.
    
    public SimplePhysicalConnectionLayer(Environment environment, Map<String, String> args) {
        super(environment, args);
    }
    
    // Methods.
    
    @Override
    protected @NotNull PhysicalEvent decoratePhysicalEvent(PhysicalEvent physicalEvent) {
        return physicalEvent;
    }
    
    @Override
    public boolean hasPhysicalConnection(AgentIdentifier a1, AgentIdentifier a2) {
        if (a1 == null || a2 == null)
            throw new NullPointerException();
        
        return true;
    }
    
    @Override
    protected boolean canBeSent(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent) {
        if (initiator == null || target == null || physicalEvent == null)
            throw new NullPointerException();
        return true;
    }
    
    @Override
    protected void scheduleInEnvironment(AgentIdentifier target, PhysicalEvent physicalEvent) {
        getEnvironment().assignEventOn(target, physicalEvent, 10L);
    }
}
