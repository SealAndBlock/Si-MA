package sima.standard.environment;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.simulation.SimaSimulation;

import java.util.Map;

/**
 * An {@link Environment} which accept all type of {@link sima.core.agent.SimaAgent}.
 */
public class SimpleEnvironment extends Environment {
    
    // Constructors.
    
    public SimpleEnvironment(String environmentName, Map<String, String> args) {
        super(environmentName, args);
    }
    
    // Methods.
    
    /**
     * @param abstractAgentIdentifier the {@link AgentIdentifier} of the agent to verify
     *
     * @return always true. Accept all type of agent.
     */
    @Override
    protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
        return true;
    }
    
    @Override
    protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
        // Nothing
    }
    
    @Override
    protected void scheduleEventProcess(AgentIdentifier target, Event event, long delay) {
        SimaSimulation.getScheduler().scheduleEvent(target, event, delay);
    }
}
