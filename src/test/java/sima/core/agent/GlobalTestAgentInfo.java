package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public abstract class GlobalTestAgentInfo extends SimaTest {

    // Static.

    protected AgentInfo AGENT_INFO;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT_INFO, "AGENT_INFO cannot be null for tests");
    }

    // Tests.

    @Test
    public void getAgentIdentifierNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getAgentIdentifier());
    }

    @Test
    public void getBehaviorsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getBehaviors());
    }

    @Test
    public void getProtocolsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getProtocols());
    }

    @Test
    public void getEnvironmentsNeverReturnsNull() {
        assertNotNull(AGENT_INFO.getEnvironments());
    }

}
