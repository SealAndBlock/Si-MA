package sima.core.protocol;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class GlobalTestProtocol extends SimaTest {

    // Static.

    protected Protocol PROTOCOL;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(PROTOCOL, "PROTOCOL cannot be null for tests");
    }

    // Tests.

    @Test
    public void getIdentifierNeverReturnsNull() {
        assertNotNull(PROTOCOL.getIdentifier());
    }

    @Test
    public void getIdentifierReturnsCorrespondingIdentifier() {
        ProtocolIdentifier protocolIdentifier = PROTOCOL.getIdentifier();
        verifyPreConditionAndExecuteTest(() -> protocolIdentifier != null,
                () -> {
                    assertEquals(PROTOCOL.getClass(), protocolIdentifier.getProtocolClass());
                    assertEquals(PROTOCOL.getProtocolTag(), protocolIdentifier.getProtocolTag());
                });
    }

    @Test
    public void resetDefaultProtocolManipulatorResetTheDefaultProtocolManipulator() {
        Class<? extends ProtocolManipulator> defaultProtocolManipulatorClass
                = PROTOCOL.getDefaultProtocolManipulator().getClass();
        PROTOCOL.setProtocolManipulator(new ProtocolManipulator.DefaultProtocolManipulator(PROTOCOL));
        PROTOCOL.resetDefaultProtocolManipulator();
        assertEquals(defaultProtocolManipulatorClass, PROTOCOL.getDefaultProtocolManipulator().getClass());
    }

    @Test
    public void getProtocolTagNeverReturnsNull() {
        assertNotNull(PROTOCOL.getProtocolTag());
    }

    @Test
    public void getAgentOwnerNeverReturnsNull() {
        assertNotNull(PROTOCOL.getAgentOwner());
    }

    @Test
    public void getProtocolManipulatorNeverReturnsNull() {
        assertNotNull(PROTOCOL.getProtocolManipulator());
    }

    @Test
    public void setProtocolManipulatorWithNullArgumentThrowsException() {
        assertThrows(NullPointerException.class, () -> PROTOCOL.setProtocolManipulator(null));
    }

    @Test
    public void setProtocolManipulatorWithNotNullArgumentNotFail() {
        notFail(() -> PROTOCOL.setProtocolManipulator(new ProtocolManipulator.DefaultProtocolManipulator(PROTOCOL)));
    }
}
