package sima.core.behavior;

import sima.core.agent.AbstractAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;

public class BehaviorTesting extends Behavior {

    // Variables.

    private int passToProcessArg = 0;
    private int passToOnStartPlaying = 0;
    private int passToInStopPlaying = 0;

    // Constructors.

    public BehaviorTesting(AbstractAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
        this.passToProcessArg++;
    }

    @Override
    public boolean canBePlayedBy(AbstractAgent agent) {
        return agent != null;
    }

    @Override
    public void onStartPlaying() {
        this.passToOnStartPlaying++;
    }

    @Override
    public void onStopPlaying() {
        this.passToInStopPlaying++;
    }

    public void reset() {
        this.passToProcessArg = 0;
        this.passToOnStartPlaying = 0;
        this.passToInStopPlaying = 0;
    }

    // Getters and Setters.

    public int getPassToProcessArg() {
        return passToProcessArg;
    }

    public int getPassToOnStartPlaying() {
        return passToOnStartPlaying;
    }

    public int getPassToInStopPlaying() {
        return passToInStopPlaying;
    }
}
