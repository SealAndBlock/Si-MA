package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
abstract class GlobalTestAgentInfo extends SimaTest {
    
    // Static.
    
    protected AgentInfo AGENT_INFO;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT_INFO, "AGENT_INFO cannot be null for tests");
    }
    
    // Tests.
    
    @Test
    void getAgentIdentifierNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getAgentIdentifier());
    }
    
    @Test
    void getBehaviorsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getBehaviors());
    }
    
    @Test
    void getProtocolsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getProtocols());
    }
    
    @Test
    void getEnvironmentsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getEnvironments());
    }
    
}
