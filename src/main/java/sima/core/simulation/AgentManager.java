package sima.core.simulation;

import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;

import java.util.List;

public interface AgentManager {

    /**
     * Add the {@link SimaAgent} to the {@link AgentManager}. If the {@code AbstractAgent} is already in the {@code AgentManager}, nothing is done.
     *
     * @param agent the agent to add
     *
     * @return true if the {@code AbstractAgent} has been added, else false.
     */
    boolean addAgent(SimaAgent agent);

    /**
     * Returns the list of all agents managed by the {@link AgentManager}. If these methods is called after the start of the simulation, therefore
     * these methods returns all agents in the simulation.
     * <p>
     * Modify the list have no impact on the AgentManager, the list returns is a copy of the list used by the manager.
     *
     * @return the list of all agents managed by the {@link AgentManager}. Never returns null.
     */
    List<SimaAgent> getAllAgents();

    /**
     * Searches if a {@link SimaAgent} is associated to the specified {@link AgentManager}.
     * <p>
     * This interface method has a not efficient default implementation. The default implementation browses the lisy of all agents returned by the
     * method {@link #getAllAgents()} and search if a {@link SimaAgent} has as {@link AgentIdentifier} the specified {@link AgentIdentifier}.
     *
     * @param agentIdentifier the agent identifier
     *
     * @return the agent which is associated to the {@link AgentIdentifier} if it exists, else null.
     */
    default SimaAgent getAgent(AgentIdentifier agentIdentifier) {
        if (agentIdentifier == null)
            return null;

        List<SimaAgent> agents = this.getAllAgents();
        SimaAgent res = null;
        for (SimaAgent agent : agents)
            if (agent.getAgentIdentifier().equals(agentIdentifier)) {
                res = agent;
                break;
            }

        return res;
    }

    /**
     * Searches if a {@link SimaAgent} has the specified unique id.
     * <p>
     * This interface method has a not efficient default implementation. The default implementation browses the lisy of all agents returned by the
     * method {@link #getAllAgents()} and search if a {@link SimaAgent} has as the specified unique id.
     *
     * @param uniqueId the unique id of a {@link SimaAgent}
     *
     * @return the agent which has the specified unique id if it exists, else null.
     */
    default SimaAgent getAgent(long uniqueId) {
        if (uniqueId < 0)
            return null;

        List<SimaAgent> agents = getAllAgents();
        SimaAgent res = null;
        for (SimaAgent agent : agents)
            if (agent.getAgentIdentifier().agentUniqueId() == uniqueId) {
                res = agent;
                break;
            }

        return res;
    }
}
