package sima.core.simulation.specific;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.EnvironmentTesting;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpecificEnvironmentWithHashCodeOnlyOnEnvironmentNameTesting extends EnvironmentTesting {

    // Constructors.

    public SpecificEnvironmentWithHashCodeOnlyOnEnvironmentNameTesting(String name, Map<String, String> args) {
        super(name, args);
    }

    public SpecificEnvironmentWithHashCodeOnlyOnEnvironmentNameTesting(int number) {
        super(number);
    }

    public SpecificEnvironmentWithHashCodeOnlyOnEnvironmentNameTesting(int number,
                                                                       List<AgentIdentifier> notAcceptedAgentList) {
        super(number, notAcceptedAgentList);
    }

    // Methods.

    @Override
    public int hashCode() {
        return Objects.hashCode(getEnvironmentName());
    }
}
