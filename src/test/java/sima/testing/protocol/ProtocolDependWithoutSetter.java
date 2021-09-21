package sima.testing.protocol;

import sima.core.agent.SimpleAgent;
import sima.core.environment.Environment;
import sima.core.environment.exchange.event.Event;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class ProtocolDependWithoutSetter extends Protocol {
    
    // Variables.
    
    private Environment environment;
    
    // Constructors.
    
    public ProtocolDependWithoutSetter(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    @Override
    public void processEvent(Event event) {
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
    
    @Override
    public void processTransportable(Transportable transportable) {
    }
}
