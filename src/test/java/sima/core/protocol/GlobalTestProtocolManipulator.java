package sima.core.protocol;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
abstract class GlobalTestProtocolManipulator extends SimaTest {
    
    // Static.
    
    protected ProtocolManipulator PROTOCOL_MANIPULATOR;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(PROTOCOL_MANIPULATOR, "PROTOCOL_MANIPULATOR cannot be null for tests");
    }
    
    // Tests.
    
    @Test
    void getManipulatedProtocolNeverReturnsNull() {
        assertNotNull(PROTOCOL_MANIPULATOR.getManipulatedProtocol());
    }
    
    @Test
    void setManipulatedProtocolWithNullArgumentThrowsException() {
        assertThrows(NullPointerException.class, () -> PROTOCOL_MANIPULATOR.setManipulatedProtocol(null));
    }
    
    @Test
    void setManipulatedProtocolWithNotNullArgumentNotFail() {
        AbstractAgent a = new AgentTesting("A", 0, 0, null);
        Protocol protocol = new ProtocolTesting("P_TAG", a, null);
        assertDoesNotThrow(() -> PROTOCOL_MANIPULATOR.setManipulatedProtocol(protocol));
    }
    
    @Test
    void resetStateNotFail() {
        assertDoesNotThrow(() -> PROTOCOL_MANIPULATOR.resetState());
    }
}
