package sima.core.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAgentIdentifier extends GlobalTestAgentIdentifier {

    // Initialisation.

    @Override
    public void verifyAndSetup() {
        AGENT_IDENTIFIER_0 = new AgentIdentifier("A_0", 0, 0);
        AGENT_IDENTIFIER_1 = new AgentIdentifier("A_1", 1, 1);
        AGENT_IDENTIFIER_0_SAME = new AgentIdentifier("A_0", 0, 0);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructAgentIdentifierWithNullAgentNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentIdentifier(null, 0, 0));
    }

    @Test
    public void constructAgentIdentifierWithNegativeAgentSequenceIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("A_TEST", -1, 0));
    }

    @Test
    public void constructAgentIdentifierWithPositiveAgentSequenceIdNotFail() {
        assertDoesNotThrow(() -> new AgentIdentifier("A_TEST", 0, 0));
    }

    @Test
    public void constructAgentIdentifierWithNegativeAgentUniqueIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("A_TEST", 0, -1));
    }

    @Test
    public void constructAgentIdentifierWithPositiveAgentUniqueIdNotFail() {
        assertDoesNotThrow(() -> new AgentIdentifier("A_TEST", 0, 0));
    }
}
