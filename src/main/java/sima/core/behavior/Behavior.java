package sima.core.behavior;

import sima.core.agent.AbstractAgent;
import sima.core.behavior.exception.BehaviorCannotBePlayedByAgentException;

import java.util.Optional;

/**
 * Represents the behavior that an {@link AbstractAgent} can have.
 * <p>
 * All inherited classes of {@link Behavior} must have the same constructor {@link #Behavior(AbstractAgent, String[])}.
 * In that way, it allow the use of java reflexivity.
 */
public abstract class Behavior {

    // Variables.

    /**
     * The agent which has the behavior.
     */
    private final Optional<AbstractAgent> agent;

    /**
     * True if the {@link #agent} is playing the behavior, else false.
     */
    private boolean isPlaying = false;

    // Constructors.

    /**
     * Initiate the behavior with the instance of the agent which will play it and an array of arguments. Verifies if
     * the specified agent can play the behavior, if it is not the case, throws a
     * {@link BehaviorCannotBePlayedByAgentException}.
     * <p>
     * All inherited classes of {@link Behavior} must have this constructor with sames arguments of this constructor.
     *
     * @param agent the agent which play the behavior
     * @param args  arguments array
     * @throws NullPointerException                   if the agent is null
     * @throws BehaviorCannotBePlayedByAgentException if the behavior cannot be played by the agent
     */
    public Behavior(AbstractAgent agent, String[] args) throws BehaviorCannotBePlayedByAgentException {
        this.agent = Optional.of(agent);

        if (!this.canBePlayedBy(this.agent.get()))
            throw new BehaviorCannotBePlayedByAgentException("The agent : " + this.agent.get() + " cannot play the " +
                    "behavior " + this.getClass().getName());

        if (args != null)
            this.processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments array
     */
    protected abstract void processArgument(String[] args);

    /**
     * Verify if the agent can play the behavior or not.
     *
     * @param agent the agent to verify
     * @return true if the agent can play the behavior, else false.
     */
    public abstract boolean canBePlayedBy(AbstractAgent agent);

    /**
     * Call when the agent must start to play the behavior. Can be call only one time (no effect after the first call).
     * You must call the method {@link #stopPlaying()} to be allowed to call again this method.
     * <p>
     * To implement the start behavior, implement the method {@link #onStartPlaying()}.
     */
    public final void startPlaying() {
        if (!this.isPlaying) {
            this.onStartPlaying();
            this.isPlaying = true;
        }
    }

    /**
     * Stop to play the behavior. The behavior must be started for that method have an effect.
     */
    public final void stopPlaying() {
        if (this.isPlaying) {
            this.onStopPlaying();
            this.isPlaying = false;
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
        return agent.orElse(null);
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
