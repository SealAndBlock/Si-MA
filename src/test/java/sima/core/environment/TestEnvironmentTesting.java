package sima.core.environment;

import org.junit.jupiter.api.Test;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.AgentTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestEnvironmentTesting extends GlobalTestEnvironment {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        List<AgentIdentifier> notAcceptedAgent = new ArrayList<>();
        
        ACCEPTED_AGENT = new AgentTesting("ACCEPT_AGENT", 0, 0, null);
        ACCEPTED_AGENT_IDENTIFIER = ACCEPTED_AGENT.getAgentIdentifier();
        NOT_ACCEPTED_AGENT_IDENTIFIER = new AgentTesting("NOT_ACCEPTED_AGENT", 1, 1, null).getAgentIdentifier();
        
        notAcceptedAgent.add(NOT_ACCEPTED_AGENT_IDENTIFIER);
        
        NOT_EVOLVING_AGENT_IDENTIFIER = new AgentTesting("NOT_EVOLVING", 2, 2, null).getAgentIdentifier();
        
        ENVIRONMENT = new EnvironmentTesting(0, notAcceptedAgent);
        ENVIRONMENT_EQUAL = new EnvironmentTesting(0, notAcceptedAgent);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructEnvironmentTestingWithNullNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new EnvironmentTesting(null, null));
    }
    
    @Test
    void constructEnvironmentTestingWithNotNullNameNotFail() {
        assertDoesNotThrow(() -> new EnvironmentTesting("ENV", null));
    }
    
    @Test
    void constructEnvironmentTestingWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new EnvironmentTesting("ENV", new HashMap<>()));
    }
    
    @Test
    void constructEventTestingNotFailWithoutList() {
        try {
            new EnvironmentTesting(0);
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void constructEventTestingNotFailWithNullList() {
        try {
            new EnvironmentTesting(0, null);
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void constructEventTestingNotFailWithNotNullList() {
        try {
            new EnvironmentTesting(0, new ArrayList<>());
        } catch (Exception e) {
            fail(e);
        }
    }
}
