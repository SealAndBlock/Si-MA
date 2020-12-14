package sima.core.agent;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAgentIdentifier extends GlobalTestAgentIdentifier {

    // Initialisation.

    @Override
    public void verifyAndSetup() {
        UUID a0 = UUID.randomUUID();
        AGENT_IDENTIFIER_0 = new AgentIdentifier(a0, "A_0", 0);
        AGENT_IDENTIFIER_1 = new AgentIdentifier(UUID.randomUUID(), "A_1", 1);
        AGENT_IDENTIFIER_0_SAME = new AgentIdentifier(a0, "A_0", 0);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructAgentIdentifierWithNullUUIDThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentIdentifier(null, "A_TEST", 0));
    }

    @Test
    public void constructAgentIdentifierWithNullAgentNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentIdentifier(UUID.randomUUID(), null, 0));
    }

    @Test
    public void constructAgentIdentifierWithNegativeAgentNumberIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier(UUID.randomUUID(), "A_TEST", -1));
    }
}
