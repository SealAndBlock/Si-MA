package sima.testing.protocol;

import sima.core.agent.SimpleAgent;
import sima.core.environment.exchange.event.Event;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

public class WrongConstructorProtocol extends Protocol {
    
    // Constructors
    
    public WrongConstructorProtocol(String protocolTag, SimpleAgent agentOwner) {
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
    public void processTransportable(Transportable transportable) {
    }
}
