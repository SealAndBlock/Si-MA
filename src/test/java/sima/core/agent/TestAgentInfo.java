package sima.core.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TestAgentInfo extends GlobalTestAgentInfo {

    // Static.

    protected static final AgentTesting AGENT_TESTING = new AgentTesting("A_0", 0, null);

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AGENT_INFO = AGENT_TESTING.getInfo();

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructAgentInfoWithNullAgentIdentifierThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentInfo(null, null, null, null));
    }

    @Test
    public void constructAgentInfoWithNullBehaviorsNotThrowsException() {
        try {
            new AgentInfo(AGENT_TESTING.getAgentIdentifier(), null, null, null);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void constructAgentInfoWithNullProtocolsNotThrowsException() {
        try {
            new AgentInfo(AGENT_TESTING.getAgentIdentifier(), null, null, null);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void constructAgentInfoWithNullEnvironmentsNotThrowsException() {
        try {
            new AgentInfo(AGENT_TESTING.getAgentIdentifier(), null, null, null);
        } catch (Exception e) {
            fail(e);
        }
    }
}