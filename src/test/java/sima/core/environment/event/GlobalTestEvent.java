package sima.core.environment.event;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AgentIdentifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class GlobalTestEvent extends SimaTest {

    // Static.

    protected Event EVENT;

    protected Event PROTOCOL_EVENT;
    protected Event NO_PROTOCOL_EVENT;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(EVENT, "EVENT cannot be null for tests");
        assertNotNull(PROTOCOL_EVENT, "PROTOCOL_EVENT cannot be null for tests");
        assertNotNull(NO_PROTOCOL_EVENT, "NO_PROTOCOL_EVENT cannot be null for tests");

        assertNotNull(PROTOCOL_EVENT.getProtocolTargeted(), "PROTOCOL_EVENT must have a protocol targeted not null");
        assertNull(NO_PROTOCOL_EVENT.getProtocolTargeted(), "NO_PROTOCOL_EVENT must have a protocol targeted null");
    }

    // Tests.

    @Test
    public void getSenderNeverReturnsNull() {
        assertNotNull(EVENT.getSender());
    }

    @Test
    public void isProtocolEventReturnsFalseIfProtocolTargetedIsNull() {
        verifyPreConditionAndExecuteTest(() -> NO_PROTOCOL_EVENT.getProtocolTargeted() == null,
                                         () -> assertFalse(NO_PROTOCOL_EVENT.isProtocolEvent()));
    }

    @Test
    public void isProtocolEventReturnsTrueIfProtocolTargetedIsNotNull() {
        verifyPreConditionAndExecuteTest(() -> PROTOCOL_EVENT.getProtocolTargeted() != null,
                                         () -> assertTrue(PROTOCOL_EVENT.isProtocolEvent()));
    }
    @Test
    public void duplicateWithNewReceiverForMessageWithNotNullContentNotThrowException() {
        assertDoesNotThrow(() -> PROTOCOL_EVENT.duplicateWithNewReceiver(new AgentIdentifier("Test", 0, 0)));
    }
    
    @Test
    public void duplicateWithNewReceiverForMessageWithNullContentNotThrowException() {
        assertDoesNotThrow(() -> NO_PROTOCOL_EVENT.duplicateWithNewReceiver(new AgentIdentifier("Test", 0, 0)));
    }
}
