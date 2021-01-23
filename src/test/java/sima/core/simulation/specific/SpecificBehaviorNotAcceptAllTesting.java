package sima.core.simulation.specific;

import sima.core.agent.AbstractAgent;
import sima.core.behavior.BehaviorTesting;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class SpecificBehaviorNotAcceptAllTesting extends BehaviorTesting {

    // Constructors.

    public SpecificBehaviorNotAcceptAllTesting(AbstractAgent agent, Map<String, String> args)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }

    // Methods.

    @Override
    public boolean canBePlayedBy(AbstractAgent agent) {
        return false;
    }
}
