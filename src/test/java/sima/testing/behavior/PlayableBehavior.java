package sima.testing.behavior;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class PlayableBehavior extends Behavior {
    
    // Constructors
    
    public PlayableBehavior(SimaAgent agent, Map<String, String> args)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
    
    
    // Methods.
    @Override
    public boolean canBePlayedBy(SimaAgent agent) {
        return true;
    }
    
    @Override
    public void onStartPlaying() {
    }
    
    @Override
    public void onStopPlaying() {
    }
    
}
