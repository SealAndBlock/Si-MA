package sima.basic.behavior;

import sima.core.agent.SimpleAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

/**
 * A {@link Behavior} which is playable by all agents. Its method {@link Behavior#canBePlayedBy(SimpleAgent)} always returns true.
 */
public class PlayableByAllAgentsBehavior extends Behavior {
    
    // Constructors.
    
    public PlayableByAllAgentsBehavior(SimpleAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
    
    // Methods.
    
    @Override
    public final boolean canBePlayedBy(SimpleAgent agent) {
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
