package sima.core.simulation.specific;

import sima.core.agent.AbstractAgent;
import sima.core.protocol.ProtocolTesting;

import java.util.Map;

public class SpecificProtocolTesting extends ProtocolTesting {

    // Constructors.

    public SpecificProtocolTesting(String protocolTag, AbstractAgent agentOwner,
                                   Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
}
