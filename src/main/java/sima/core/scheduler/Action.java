package sima.core.scheduler;

import sima.core.agent.AbstractAgent;

import java.util.UUID;

public abstract class Action {

    // Variables.

    private final UUID executorAgent;

    // Constructors.

    public Action(UUID executorAgent) {
        this.executorAgent = executorAgent;
    }

    // Methods.

    /**
     * The method to call to execute the {@link Action}.
     */
    public abstract void execute();

}
