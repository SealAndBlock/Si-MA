package sima.core.simulation.specific;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.EnvironmentTesting;

import java.util.List;
import java.util.Map;

public class SpecificEnvironmentNotAcceptAgentTesting extends EnvironmentTesting {

    // Constructors.

    public SpecificEnvironmentNotAcceptAgentTesting(String name, Map<String, String> args) {
        super(name, args);
    }

    public SpecificEnvironmentNotAcceptAgentTesting(int number) {
        super(number);
    }

    public SpecificEnvironmentNotAcceptAgentTesting(int number,
                                                    List<AgentIdentifier> notAcceptedAgentList) {
        super(number, notAcceptedAgentList);
    }

    // Methods.

    @Override
    public synchronized boolean acceptAgent(AgentIdentifier evolvingAgentIdentifier) {
        return false;
    }
}
