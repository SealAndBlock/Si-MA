package sima.core.protocol;

import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDefaultProtocolManipulator extends GlobalTestProtocolManipulator {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AbstractAgent a = new AgentTesting("A_0", 0,0, null);
        Protocol protocol = new ProtocolTesting("P_TAG_0", a, null);
        PROTOCOL_MANIPULATOR = new ProtocolManipulator.DefaultProtocolManipulator(protocol);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructDefaultProtocolManipulatorWithNullProtocolThrowsException() {
        assertThrows(NullPointerException.class, () -> new ProtocolManipulator.DefaultProtocolManipulator(null));
    }

    @Test
    public void constructDefaultProtocolManipulatorWithNotNullProtocolThrowsException() {
        AbstractAgent a = new AgentTesting("A_0", 0, 0,null);
        Protocol protocol = new ProtocolTesting("P_TAG_0", a, null);
        assertDoesNotThrow(() -> new ProtocolManipulator.DefaultProtocolManipulator(protocol));
    }
}
