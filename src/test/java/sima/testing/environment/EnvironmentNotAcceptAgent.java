package sima.testing.environment;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;

import java.util.Map;

public class EnvironmentNotAcceptAgent extends Environment {
    
    // Constructors.
    
    public EnvironmentNotAcceptAgent(String environmentName, Map<String, String> args) {
        super(environmentName, args);
    }
    
    // Methods.
    
    @Override
    protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
        return false;
    }
    
    @Override
    protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
    }
    
    @Override
    protected void scheduleEventReception(AgentIdentifier receiver, Event event, long delay) {
    }
}
