package sima.core.behavior;

import sima.core.agent.AbstractAgent;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the sima.core.behavior that an {@link AbstractAgent} can have.
 * <p>
 * All inherited classes of {@link Behavior} must have the same constructor {@link #Behavior(AbstractAgent, Map)}.
 * In that way, it allow the use of java reflexivity.
 */
public abstract class Behavior {

    // Variables.

    /**
     * The sima.core.agent which has the sima.core.behavior.
     */
    private final AbstractAgent agent;

    /**
     * True if the {@link #agent} is playing the sima.core.behavior, else false.
     */
    private boolean isPlaying = false;

    // Constructors.

    /**
     * Initiate the sima.core.behavior with the instance of the sima.core.agent which will play it and an map of arguments. Verifies if
     * the specified sima.core.agent can play the sima.core.behavior, if it is not the case, throws a
     * {@link BehaviorCannotBePlayedByAgentException}.
     * <p>
     * All inherited classes of {@link Behavior} must have this constructor with sames arguments of this constructor.
     *
     * @param agent the sima.core.agent which play the sima.core.behavior
     * @param args  arguments map (map argument name with the argument)
     * @throws NullPointerException                   if the sima.core.agent is null
     * @throws BehaviorCannotBePlayedByAgentException if the sima.core.behavior cannot be played by the sima.core.agent
     */
    public Behavior(AbstractAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
        this.agent = Optional.of(agent).get();

        if (!canBePlayedBy(agent))
            throw new BehaviorCannotBePlayedByAgentException("The sima.core.agent : " + agent + " cannot play the " +
                    "behavior " + getClass().getName());

        if (args != null)
            processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments map (map argument name with the argument)
     */
    protected abstract void processArgument(Map<String, String> args);

    /**
     * Verify if the sima.core.agent can play the sima.core.behavior or not.
     *
     * @param agent the sima.core.agent to verify
     * @return true if the sima.core.agent can play the sima.core.behavior, else false.
     */
    public abstract boolean canBePlayedBy(AbstractAgent agent);

    /**
     * Call when the sima.core.agent must start to play the sima.core.behavior. Can be call only one time (no effect after the first call).
     * You must call the method {@link #stopPlaying()} to be allowed to call again this method.
     * <p>
     * To implement the start sima.core.behavior, implement the method {@link #onStartPlaying()}.
     */
    public final void startPlaying() {
        if (!isPlaying()) {
            onStartPlaying();
            isPlaying = true;
        }
    }

    /**
     * Stop to play the sima.core.behavior. The sima.core.behavior must be started for that method have an effect.
     */
    public final void stopPlaying() {
        if (isPlaying()) {
            onStopPlaying();
            isPlaying = false;
        }
    }

    /**
     * Method called in the method {@link #startPlaying()}.
     */
    public abstract void onStartPlaying();

    /**
     * Method called in the method {@link #stopPlaying()}  .
     */
    public abstract void onStopPlaying();

    // Getters and Setters.

    public AbstractAgent getAgent() {
        return agent;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
