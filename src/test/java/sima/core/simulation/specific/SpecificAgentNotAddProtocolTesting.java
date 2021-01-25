package sima.core.simulation.specific;

import sima.core.agent.AgentTesting;
import sima.core.protocol.Protocol;

import java.util.Map;

public class SpecificAgentNotAddProtocolTesting extends AgentTesting {

    // Constructors.

    public SpecificAgentNotAddProtocolTesting(String agentName, int sequenceId, int uniqueId,
                                              Map<String, String> args) {
        super(agentName, sequenceId, uniqueId, args);
    }

    // Methods.

    @Override
    public synchronized boolean addProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                            Map<String, String> args) {
        return false;
    }
}
