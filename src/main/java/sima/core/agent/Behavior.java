package sima.core.agent;

import java.util.Optional;

/**
 * Represents the behavior that an {@link AbstractAgent} can have.
 * <p>
 * All sub classes of {@link Behavior} must have this constructor with only one argument which is an
 * {@link AbstractAgent}.
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
     * Initiate the behavior with the instance of the agent which will play it.
     * <p>
     * All sub classes of {@link Behavior} must have this constructors with only one argument which is an
     * {@link AbstractAgent}.
     *
     * @param agent the agent which play the behavior
     * @throws NullPointerException if the agent is null
     */
    public Behavior(AbstractAgent agent) {
        this.agent = Optional.of(agent);
    }

    // Methods.

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
