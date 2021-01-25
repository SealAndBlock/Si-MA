package sima.core.agent;

import org.junit.jupiter.api.Test;
import sima.core.environment.event.EventTesting;
import sima.core.protocol.ProtocolTesting;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestAgentTesting extends GlobalTestAbstractAgent {

    // Initialisation.

    @Override
    public void verifyAndSetup() {
        AGENT_0 = new AgentTesting("AGENT_0", 0, 0, null);
        AGENT_1 = new AgentTesting("AGENT_1", 1, 1, null);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructAgentTestingWithNegativeSequenceIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentTesting("A_0", -1, 0, new HashMap<>()));
    }

    @Test
    public void constructAgentTestingWithPositiveSequenceIdNotFail() {
        assertDoesNotThrow(() -> new AgentTesting("A_0", 0, 0, new HashMap<>()));
    }

    @Test
    public void constructAgentTestingWithNegativeUniqueIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentTesting("A_0", 0, -1, new HashMap<>()));
    }

    @Test
    public void constructAgentTestingWithPositiveUniqueIdNotFail() {
        assertDoesNotThrow(() -> new AgentTesting("A_0", 0, 0, new HashMap<>()));
    }

    @Test
    public void constructAgentTestingWithNullAgentNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentTesting(null, 0, 0, null));
    }

    @Test
    public void constructAgentWithNullArgsNotThrowsException() {
        assertDoesNotThrow(() -> new AgentTesting("A_0", 0, 0, null));

    }

    @Test
    public void processEventWithEventWithNoProtocolTargetedThrowsException() {
        AGENT_0.addProtocol(ProtocolTesting.class, "P_0", null);
        AGENT_0.start();

        EventTesting e = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), null);
        assertThrows(UnsupportedOperationException.class, () -> AGENT_0.processEvent(e));
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
