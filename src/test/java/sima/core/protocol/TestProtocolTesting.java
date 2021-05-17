package sima.core.protocol;

import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventTesting;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestProtocolTesting extends GlobalTestProtocol {
    
    // Static.
    
    protected AbstractAgent AGENT;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        AGENT = new AgentTesting("A_0", 0, 0, null);
        PROTOCOL = new ProtocolTesting("TAG_P_TEST", AGENT, null);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructProtocolTestingWithNullTagThrowsException() {
        assertThrows(NullPointerException.class, () -> new ProtocolTesting(null, AGENT, null));
    }
    
    @Test
    void constructProtocolTestingWithNullAgentThrowsException() {
        assertThrows(NullPointerException.class, () -> new ProtocolTesting("TAG", null, null));
    }
    
    @Test
    void constructProtocolTestingWithNullArgsNotFail() {
        assertDoesNotThrow(() -> new ProtocolTesting("TAG", AGENT, null));
    }
    
    @Test
    void constructProtocolTestingWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new ProtocolTesting("TAG", AGENT, new HashMap<>()));
    }
    
    @Test
    void processEventWithNullEventNotFail() {
        assertDoesNotThrow(() -> PROTOCOL.processEvent(null));
    }
    
    @Test
    void processEventWithNotNullEventNotFail() {
        assertDoesNotThrow(() -> {
            AbstractAgent s = new AgentTesting("SENDER", 0, 0, null);
            AbstractAgent r = new AgentTesting("RECEIVER", 0, 0, null);
            Event event = new EventTesting(s.getAgentIdentifier(), r.getAgentIdentifier(), PROTOCOL.getIdentifier());
            PROTOCOL.processEvent(event);
        });
    }
    
}
