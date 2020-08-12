package sima.core.agent;

import java.util.Optional;

/**
 * Represents the behavior that an {@link AbstractAgent} can have.
 * <p>
 * All sub classes of {@link Behavior} must have this constructors with only one argument which is an
 * {@link AbstractAgent}.
 */
public abstract class Behavior {

    // Variables.

    private final Optional<AbstractAgent> agent;
    private boolean isStarted = false;

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
    public final void play() {
        if (!this.isStarted) {
            this.onStartPlaying();
            this.isStarted = true;
        }
    }

    /**
     * Stop to play the behavior. The behavior must be started for that method have an effect.
     */
    public final void stopPlaying() {
        if (this.isStarted) {
            this.onStopPlaying();
            this.isStarted = false;
        }
    }

    /**
     * Method called in the method {@link #play()}.
     */
    public abstract void onStartPlaying();

    /**
     * Method called in the method {@link #stopPlaying()}.
     */
    public abstract void onStopPlaying();

    // Getters and Setters.

    public AbstractAgent getAgent() {
        return agent.orElse(null);
    }

    public boolean isStarted() {
        return isStarted;
    }
}
