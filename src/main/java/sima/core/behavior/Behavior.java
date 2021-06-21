package sima.core.behavior;

import sima.core.agent.SimpleAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;
import java.util.Optional;

import static sima.core.simulation.SimaSimulation.SimaLog;

/**
 * Represents the sima.core.behavior that an {@link SimpleAgent} can have.
 * <p>
 * All inherited classes of {@link Behavior} must have the same constructor {@link #Behavior(SimpleAgent, Map)}. In
 * that way, it allow the use of java reflexivity.
 */
public abstract class Behavior {

    // Variables.

    /**
     * The sima.core.agent which has the sima.core.behavior.
     */
    private final SimpleAgent agent;

    /**
     * True if the {@link #agent} is playing the sima.core.behavior, else false.
     */
    private boolean isPlaying = false;

    // Constructors.

    /**
     * Initiate the sima.core.behavior with the instance of the sima.core.agent which will play it and an map of
     * arguments. Verifies if the specified sima.core.agent can play the sima.core.behavior, if it is not the case,
     * throws a {@link BehaviorCannotBePlayedByAgentException}.
     * <p>
     * All inherited classes of {@link Behavior} must have this constructor with sames arguments of this constructor.
     *
     * @param agent the sima.core.agent which play the sima.core.behavior
     * @param args  arguments map (map argument name with the argument)
     * @throws NullPointerException                   if the sima.core.agent is null
     * @throws BehaviorCannotBePlayedByAgentException if the sima.core.behavior cannot be played by the sima.core.agent
     */
    protected Behavior(SimpleAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        this.agent = Optional.of(agent).get();

        if (!canBePlayedBy(agent))
            throw new BehaviorCannotBePlayedByAgentException("The sima.core.agent : " + agent + " cannot play the " +
                                                                     "behavior " + getClass().getName());
    }

    // Methods.

    @Override
    public String toString() {
        return "[Behavior - " +
                "class=" + this.getClass().getName() +
                ", agent=" + agent + "]";
    }

    /**
     * Verify if the sima.core.agent can play the sima.core.behavior or not.
     *
     * @param agent the sima.core.agent to verify
     * @return true if the sima.core.agent can play the sima.core.behavior, else false.
     */
    public abstract boolean canBePlayedBy(SimpleAgent agent);

    /**
     * Call when the sima.core.agent must start to play the sima.core.behavior. Can be call only one time (no effect
     * after the first call). You must call the method {@link #stopPlaying()} to be allowed to call again this method.
     * <p>
     * To implement the start sima.core.behavior, implement the method {@link #onStartPlaying()}.
     */
    public final void startPlaying() {
        if (!isPlaying()) {
            onStartPlaying();
            setStartPlaying();
        }
    }

    private void setStartPlaying() {
        isPlaying = true;
        SimaLog.info(agent + " START PLAYING " + this);
    }

    /**
     * Method called in the method {@link #startPlaying()}.
     */
    public abstract void onStartPlaying();

    /**
     * Stop to play the sima.core.behavior. The sima.core.behavior must be started for that method have an effect.
     */
    public final void stopPlaying() {
        if (isPlaying()) {
            onStopPlaying();
            setStopPlaying();
        }
    }

    private void setStopPlaying() {
        isPlaying = false;
        SimaLog.info(agent + " STOP PLAYING " + this);
    }

    /**
     * Method called in the method {@link #stopPlaying()}  .
     */
    public abstract void onStopPlaying();

    // Getters and Setters.

    public SimpleAgent getAgent() {
        return agent;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
