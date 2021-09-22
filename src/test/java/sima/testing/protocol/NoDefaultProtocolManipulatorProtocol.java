package sima.testing.protocol;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class NoDefaultProtocolManipulatorProtocol extends Protocol {
    
    // Constructors.
    
    public NoDefaultProtocolManipulatorProtocol(String protocolTag, SimaAgent agentOwner,
                                                Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    @Override
    public void processEvent(Event event) {
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return null;
    }
    
    @Override
    public void processEventTransportable(EventTransportable eventTransportable) {
    }
}
