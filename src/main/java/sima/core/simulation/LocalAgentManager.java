package sima.core.simulation;

import sima.core.agent.AbstractAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is an implementation of the interface {@link AgentManager} for a {@link SimaSimulation} running on a one
 * process simulation. All instances of {@link AbstractAgent} in the simulation are in the same process and JVM.
 */
public class LocalAgentManager implements AgentManager {

    // Variables.

    /**
     * The set of all managed agents.
     */
    private final Set<AbstractAgent> managedAgents;

    // Constructors.

    public LocalAgentManager() {
        this.managedAgents = new HashSet<>();
    }

    // Methods.

    @Override
    public boolean addAgent(AbstractAgent agent) {
        return this.managedAgents.add(agent);
    }

    @Override
    public List<AbstractAgent> getAllAgents() {
        return new ArrayList<>(this.managedAgents);
    }
}
