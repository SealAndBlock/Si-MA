package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
abstract class GlobalTestAgentIdentifier extends SimaTest {
    
    // Static.
    
    protected static AgentIdentifier AGENT_IDENTIFIER_0;
    protected static AgentIdentifier AGENT_IDENTIFIER_1;
    
    /**
     * Not the same instance of {@link #AGENT_IDENTIFIER_0} but has the same field of it.
     */
    protected static AgentIdentifier AGENT_IDENTIFIER_0_SAME;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT_IDENTIFIER_0, "AGENT_IDENTIFIER_0 cannot be null for tests");
        assertNotNull(AGENT_IDENTIFIER_0_SAME, "AGENT_IDENTIFIER_0 cannot be null for tests");
        assertNotNull(AGENT_IDENTIFIER_1, "AGENT_IDENTIFIER_1 cannot be null for tests");
        assertNotSame(AGENT_IDENTIFIER_0, AGENT_IDENTIFIER_1, "AGENT_IDENTIFIER_0 cannot be the same instance of " +
                "AGENT_IDENTIFIER_1 for tests");
        assertNotSame(AGENT_IDENTIFIER_0, AGENT_IDENTIFIER_0_SAME, "AGENT_IDENTIFIER_0 cannot be the same " +
                "instance of AGENT_IDENTIFIER_0_SAME for tests.");
    }
    
    @Test
    void sameInstanceOfAgentIdentifierIsEqualToItSelf() {
        assertEquals(AGENT_IDENTIFIER_0, AGENT_IDENTIFIER_0);
    }
    
    @Test
    void twoAgentIdentifierDifferentAreNotEqual() {
        assertNotEquals(AGENT_IDENTIFIER_0, AGENT_IDENTIFIER_1);
    }
    
    @Test
    void twoAgentIdentifierWithTheSameFieldsAreEquals() {
        assertEquals(AGENT_IDENTIFIER_0, AGENT_IDENTIFIER_0_SAME);
    }
    
    @Test
    void getAgentNameNeverReturnsNull() {
        assertNotNull(AGENT_IDENTIFIER_0.getAgentName());
    }
    
    @Test
    void getAgentUniqueIdNeverReturnsNegativeNumber() {
        assertTrue(AGENT_IDENTIFIER_0.getAgentUniqueId() >= 0);
    }
    
    @Test
    void getAgentSequenceIdNeverReturnsNegativeNumber() {
        assertTrue(AGENT_IDENTIFIER_0.getAgentSequenceId() >= 0);
    }
}
