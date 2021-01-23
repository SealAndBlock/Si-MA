package sima.core.simulation.specific;

import sima.core.agent.AbstractAgent;
import sima.core.behavior.BehaviorTesting;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class SpecificBehaviorTesting extends BehaviorTesting {

    // Constructors.

    public SpecificBehaviorTesting(AbstractAgent agent, Map<String, String> args)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
}
