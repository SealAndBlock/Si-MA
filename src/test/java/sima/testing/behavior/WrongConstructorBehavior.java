package sima.testing.behavior;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

public class WrongConstructorBehavior extends Behavior {
    
    // Constructors.
    
    public WrongConstructorBehavior(SimaAgent agent)
            throws BehaviorCannotBePlayedByAgentException {
        super(agent, null);
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
