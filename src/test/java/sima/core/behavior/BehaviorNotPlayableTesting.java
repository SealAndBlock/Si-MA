package sima.core.behavior;

import sima.core.agent.AbstractAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

/**
 * class for test. Implement a behavior that any agent cannot play.
 */
public class BehaviorNotPlayableTesting extends Behavior {

    // Constructors.

    public BehaviorNotPlayableTesting(AbstractAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
        // Nothing
    }

    @Override
    public boolean canBePlayedBy(AbstractAgent agent) {
        // Cannot be play by any agent
        return false;
    }

    @Override
    public void onStartPlaying() {
        // Nothing
    }

    @Override
    public void onStopPlaying() {
        // Nothing
    }
}

