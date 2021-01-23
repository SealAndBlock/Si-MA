package sima.core.agent;

import org.junit.jupiter.api.Test;
import sima.core.environment.event.EventTesting;
import sima.core.protocol.ProtocolTesting;

import static org.junit.jupiter.api.Assertions.*;

public class TestAgentTesting extends GlobalTestAbstractAgent {

    // Initialisation.

    @Override
    public void verifyAndSetup() {
        AGENT_0 = new AgentTesting("AGENT_0", 0, null);
        AGENT_1 = new AgentTesting("AGENT_1", 1, null);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructAgentTestingWithNegativeNumberIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentTesting("A_0", -1, null));
    }

    @Test
    public void constructAgentTestingWithNullAgentNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentTesting(null, 0, null));
    }

    @Test
    public void constructAgentWithNullArgsNotThrowsException() {
        try {
            new AgentTesting("A_0", 0, null);
        } catch (Exception e) {
            fail(e);
        }

    }

    @Test
    public void processEventCallTreatNoProtocolEventMethodIfEventHasNoProtocolTargeted() {
        String p0 = "P_0";
        AGENT_0.addProtocol(ProtocolTesting.class, p0, null);

        AGENT_0.start();

        EventTesting e = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), null);

        AGENT_0.processEvent(e);

        AgentTesting A0 = (AgentTesting) AGENT_0;
        assertEquals(1, A0.getPassToProcessNoProtocolEvent());
    }

    @Test
    public void passToOnStartWhenAgentIsStarted() {
        AGENT_0.start();

        assertEquals(1, ((AgentTesting) AGENT_0).getPassToOnStart());
    }

    @Test
    public void passToOnKillWhenAgentIsKilled() {
        AGENT_0.kill();

        assertEquals(1, ((AgentTesting) AGENT_0).getPassToOnKill());
    }
}
