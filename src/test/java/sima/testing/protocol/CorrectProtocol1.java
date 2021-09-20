package sima.testing.protocol;

import sima.core.agent.SimpleAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.Transportable;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class CorrectProtocol1 extends Protocol {
    
    // Constructors.
    
    public CorrectProtocol1(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
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
