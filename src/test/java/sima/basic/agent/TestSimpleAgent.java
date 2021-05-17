package sima.basic.agent;

import org.junit.jupiter.api.Test;
import sima.core.agent.GlobalTestAbstractAgent;
import sima.core.environment.event.EventTesting;
import sima.core.protocol.ProtocolTesting;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestSimpleAgent extends GlobalTestAbstractAgent {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        AGENT_0 = new SimpleAgent("AGENT_0", 0, 0, null);
        AGENT_1 = new SimpleAgent("AGENT_1", 1, 1, null);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructSimpleAgentWithNullNameThrowsException() {
        Map<String, String> hashMap = new HashMap<>();
        assertThrows(NullPointerException.class, () -> new SimpleAgent(null, 0, 0, hashMap));
    }
    
    @Test
    void constructSimpleAgentWithNegativeSequenceIdThrowsException() {
        Map<String, String> hashMap = new HashMap<>();
        assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("AGENT", -1, 0, hashMap));
    }
    
    @Test
    void constructSimpleAgentDoesNotThrowsException() {
        Map<String, String> hashMap = new HashMap<>();
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, hashMap));
    }
    
    @Test
    void constructSimpleAgentWithNegativeUniqueIdThrowsException() {
        Map<String, String> hashMap = new HashMap<>();
        assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("AGENT", 0, -1, hashMap));
    }
    
    @Test
    void constructSimpleAgentWithNullArgsDoesNotThrowsException() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, null));
    }
    
    @Test
    void processEventWithEventWithNoProtocolTargetedThrowsException() {
        AGENT_0.addProtocol(ProtocolTesting.class, "P_0", null);
        AGENT_0.start();
        
        EventTesting e = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), null);
        assertThrows(UnsupportedOperationException.class, () -> AGENT_0.processEvent(e));
    }
}
