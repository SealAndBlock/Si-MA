package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.SimpleAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is an implementation of the interface {@link AgentManager} for a {@link SimaSimulation} running on a one process simulation. All
 * instances of {@link SimpleAgent} in the simulation are in the same process and JVM.
 */
public class LocalAgentManager implements AgentManager {
    
    // Variables.
    
    /**
     * The set of all managed agents.
     */
    private final Set<SimpleAgent> managedAgents;
    
    // Constructors.
    
    public LocalAgentManager() {
        this.managedAgents = new HashSet<>();
    }
    
    // Methods.
    
    @Override
    public boolean addAgent(SimpleAgent agent) {
        if (agent == null)
            return false;
        return this.managedAgents.add(agent);
    }
    
    @Override
    public @NotNull List<SimpleAgent> getAllAgents() {
        return new ArrayList<>(this.managedAgents);
    }
}
