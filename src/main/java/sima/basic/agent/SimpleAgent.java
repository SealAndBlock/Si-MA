package sima.basic.agent;

import sima.core.agent.AbstractAgent;

import java.util.Map;

public class SimpleAgent extends AbstractAgent {
    
    // Constructors.
    
    public SimpleAgent(String agentName, int sequenceId, int uniqueId, Map<String, String> args) {
        super(agentName, sequenceId, uniqueId, args);
    }
    
    // Methods.
    
    @Override
    protected void onnStart() {
        // Nothing.
    }
    
    @Override
    protected void onKill() {
        // Nothing
    }
}
