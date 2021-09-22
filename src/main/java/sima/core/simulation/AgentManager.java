package sima.core.simulation;

import sima.core.agent.SimaAgent;

import java.util.List;

public interface AgentManager {

    /**
     * Add the {@link SimaAgent} to the {@link AgentManager}. If the {@code AbstractAgent} is already in the {@code
     * AgentManager}, nothing is done.
     *
     * @param agent the agent to add
     * @return true if the {@code AbstractAgent} has been added, else false.
     */
    boolean addAgent(SimaAgent agent);

    /**
     * Returns the list the list of all agents managed by the {@link AgentManager}. If this methods is called after the
     * start of the simulation, therefore this methods returns all agents in the simulation.
     * <p>
     * Modify the list have no impact on the AgentManager, the list returns is a copy of the list used by the manager.
     *
     * @return the list of all agents managed by the {@link AgentManager}. Never returns null.
     */
    List<SimaAgent> getAllAgents();
}
