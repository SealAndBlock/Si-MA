package sima.testing.behavior;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class NotPlayableBehavior extends Behavior {
    
    // Constructors.
    
    public NotPlayableBehavior(SimaAgent agent, Map<String, String> args)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }
    
    // Methods.
    
    @Override
    public boolean canBePlayedBy(SimaAgent agent) {
        return false;
    }
    
    @Override
    public void onStartPlaying() {
    }
    
    @Override
    public void onStopPlaying() {
    }
    
}
