package sima.core.behavior;

import sima.core.agent.SimaAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;
import java.util.Optional;

import static sima.core.simulation.SimaSimulation.SimaLog;

/**
 * Represents the {@link Behavior} that an {@link SimaAgent} can have.
 * <p>
 * All inherited classes of {@link Behavior} must have the same constructor {@link #Behavior(SimaAgent, Map)}. In that way, it allows the use of java
 * reflexivity.
 */
public abstract class Behavior {

    // Variables.

    /**
     * The {@link SimaAgent} which has the {@link Behavior}.
     */
    private final SimaAgent agent;

    /**
     * True if the {@link #agent} is playing the {@link Behavior}, else false.
     */
    private boolean isPlaying = false;

    // Constructors.

    /**
     * Initiate the {@link Behavior} with the instance of the {@link SimaAgent} which will play it and a map of arguments. Verifies if the specified
     * {@link SimaAgent} can play the {@link Behavior}, if it is not the case, throws a {@link BehaviorCannotBePlayedByAgentException}.
     * <p>
     * All inherited classes of {@link Behavior} must have this constructor with sames arguments of this constructor.
     *
     * @param agent the {@link SimaAgent} which play the {@link Behavior}
     * @param args  arguments map (map argument name with the argument)
     *
     * @throws NullPointerException                   if the {@link SimaAgent} is null
     * @throws BehaviorCannotBePlayedByAgentException if the {@link Behavior} cannot be played by the {@link SimaAgent}
     */
    protected Behavior(SimaAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        this.agent = Optional.of(agent).get();

        if (!canBePlayedBy(agent))
            throw new BehaviorCannotBePlayedByAgentException("The {@link SimaAgent} : " + agent + " cannot play the " +
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
     * Verify if the {@link SimaAgent} can play the {@link Behavior} or not.
     *
     * @param agent the {@link SimaAgent} to verify
     *
     * @return true if the {@link SimaAgent} can play the {@link Behavior}, else false.
     */
    public abstract boolean canBePlayedBy(SimaAgent agent);

    /**
     * Call when the {@link SimaAgent} must start to play the {@link Behavior}. Can be call only one time (no effect after the first call). You must
     * call the method {@link #stopPlaying()} to be allowed to call again this method.
     * <p>
     * To implement the start {@link Behavior}, implement the method {@link #onStartPlaying()}.
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
     * Stop to play the {@link Behavior}. The {@link Behavior} must be started for that method has an effect.
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

    public SimaAgent getAgent() {
        return agent;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
