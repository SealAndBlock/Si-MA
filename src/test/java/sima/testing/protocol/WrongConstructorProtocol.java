package sima.testing.protocol;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.TransportableInEvent;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

public class WrongConstructorProtocol extends Protocol {
    
    // Constructors
    
    public WrongConstructorProtocol(String protocolTag, SimaAgent agentOwner) {
        super(protocolTag, agentOwner, null);
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
    public void processEventTransportable(TransportableInEvent transportableInEvent) {
    }
}
