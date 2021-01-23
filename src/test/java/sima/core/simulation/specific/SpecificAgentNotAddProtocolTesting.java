package sima.core.simulation.specific;

import sima.core.agent.AgentTesting;
import sima.core.protocol.Protocol;

import java.util.Map;

public class SpecificAgentNotAddProtocolTesting extends AgentTesting {

    // Constructors.

    public SpecificAgentNotAddProtocolTesting(String agentName, int numberId, Map<String, String> args) {
        super(agentName, numberId, args);
    }

    // Methods.

    @Override
    public synchronized boolean addProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                            Map<String, String> args) {
        return false;
    }
}
