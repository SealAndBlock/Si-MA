package sima.core.behavior;

import sima.core.agent.AbstractAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BehaviorTesting extends Behavior {

    // Variables.

    private int passToOnStartPlaying;
    private int passToOnStopPlaying;

    public static Set<AbstractAgent> NOT_PLAYABLE_AGENT_LIST = new HashSet<>();

    // Constructors.

    public BehaviorTesting(AbstractAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        super(agent, args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
    }

    @Override
    public boolean canBePlayedBy(AbstractAgent agent) {
        return agent != null && !NOT_PLAYABLE_AGENT_LIST.contains(agent);
    }

    @Override
    public void onStartPlaying() {
        this.passToOnStartPlaying++;
    }

    @Override
    public void onStopPlaying() {
        this.passToOnStopPlaying++;
    }

    // Getters and Setters.

    public int getPassToOnStartPlaying() {
        return passToOnStartPlaying;
    }

    public int getPassToOnStopPlaying() {
        return passToOnStopPlaying;
    }
}
