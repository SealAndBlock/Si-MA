package sima.testing.protocol;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
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
    public void onOwnerStart() {
        // Nothing.
    }

    @Override
    public void onOwnerKill() {
        // Nothing.
    }

    @Override
    public void processEvent(Event event) {
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return null;
    }
}
