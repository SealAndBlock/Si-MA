package sima.core.simulation.specific;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.EnvironmentTesting;

import java.util.List;
import java.util.Map;

public class SpecificEnvironmentTesting extends EnvironmentTesting {

    // Constructors.

    public SpecificEnvironmentTesting(String name, Map<String, String> args) {
        super(name, args);
    }

    public SpecificEnvironmentTesting(int number) {
        super(number);
    }

    public SpecificEnvironmentTesting(int number, List<AgentIdentifier> notAcceptedAgentList) {
        super(number, notAcceptedAgentList);
    }
}
