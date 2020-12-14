package sima.core.protocol;

import sima.core.agent.AbstractAgent;

import java.util.Map;

public class ProtocolWithoutDefaultProtocolManipulatorTesting extends ProtocolTesting {

    // Constructors.

    public ProtocolWithoutDefaultProtocolManipulatorTesting(String protocolTag, AbstractAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    // Methods.

    @Override
    protected ProtocolManipulator getDefaultProtocolManipulator() {
        return null;
    }
}
