package sima.testing.behavior;

import sima.core.agent.SimpleAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class NotPlayableBehavior extends Behavior {
    
    // Constructors.
    
    public NotPlayableBehavior(SimpleAgent agent, Map<String, String> args)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
    
    // Methods.
    
    @Override
    public boolean canBePlayedBy(SimpleAgent agent) {
        return false;
    }
    
    @Override
    public void onStartPlaying() {
    }
    
    @Override
    public void onStopPlaying() {
    }
    
}
