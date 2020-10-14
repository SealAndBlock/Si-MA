package sima.core.scheduler;

import sima.core.agent.AgentIdentifier;

public abstract class Action {

    // Variables.

    /**
     * The agent which executes the action.
     * <p>
     * An {@code Action} is not necessarily execute by an agent.
     * <p>
     * When an {@code Action} is executed by an {@link sima.core.agent.AbstractAgent}, it is because the method called
     * in the method {@link #execute()} is a method of the executor agent.
     */
    private final AgentIdentifier executorAgent;

    // Constructors.

    public Action(AgentIdentifier executorAgent) {
        this.executorAgent = executorAgent;
    }

    // Methods.

    /**
     * The method to call to execute the {@link Action}.
     */
    public abstract void execute();

    // Getters and Setters.

    public AgentIdentifier getExecutorAgent() {
        return executorAgent;
    }
}
