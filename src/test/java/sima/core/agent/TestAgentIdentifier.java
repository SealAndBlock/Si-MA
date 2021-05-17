package sima.core.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestAgentIdentifier extends GlobalTestAgentIdentifier {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        AGENT_IDENTIFIER_0 = new AgentIdentifier("A_0", 0, 0);
        AGENT_IDENTIFIER_1 = new AgentIdentifier("A_1", 1, 1);
        AGENT_IDENTIFIER_0_SAME = new AgentIdentifier("A_0", 0, 0);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructAgentIdentifierWithNullAgentNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new AgentIdentifier(null, 0, 0));
    }
    
    @Test
    void constructAgentIdentifierWithNegativeAgentSequenceIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("A_TEST", -1, 0));
    }
    
    @Test
    void constructAgentIdentifierWithPositiveAgentSequenceIdNotFail() {
        assertDoesNotThrow(() -> new AgentIdentifier("A_TEST", 0, 0));
    }
    
    @Test
    void constructAgentIdentifierWithNegativeAgentUniqueIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("A_TEST", 0, -1));
    }
    
    @Test
    void constructAgentIdentifierWithPositiveAgentUniqueIdNotFail() {
        assertDoesNotThrow(() -> new AgentIdentifier("A_TEST", 0, 0));
    }
}
