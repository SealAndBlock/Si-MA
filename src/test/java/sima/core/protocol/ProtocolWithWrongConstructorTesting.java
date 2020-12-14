package sima.core.protocol;

import sima.core.agent.AbstractAgent;

public class ProtocolWithWrongConstructorTesting extends ProtocolTesting {

    // Constructors.

    public ProtocolWithWrongConstructorTesting(String protocolTag, AbstractAgent agentOwner) {
        super(protocolTag, agentOwner, null);
    }
}
