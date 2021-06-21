package sima.core.protocol;

import sima.core.agent.SimpleAgent;

import java.util.Map;

public abstract class NoProtocolManipulatorProtocol extends Protocol {
    
    // Constructors.
    
    protected NoProtocolManipulatorProtocol(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
    
    @Override
    public void setProtocolManipulator(ProtocolManipulator protocolManipulator) {
        throw new UnsupportedOperationException("For a " + this.getClass() + " it is impossible to set a ProtocolManipulator");
    }
}
