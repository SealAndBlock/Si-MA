package sima.basic.behavior;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

/**
 * A {@link Behavior} which is playable by all agents. Its method {@link Behavior#canBePlayedBy(SimaAgent)} always returns true.
 */
public class PlayableByAllAgentsBehavior extends Behavior {
    
    // Constructors.
    
    public PlayableByAllAgentsBehavior(SimaAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
    
    // Methods.
    
    @Override
    public final boolean canBePlayedBy(SimaAgent agent) {
        return true;
    }
    
    @Override
    public void onStartPlaying() {
        // Nothing to do.
    }
    
    @Override
    public void onStopPlaying() {
        // Nothing to do.
    }
}
