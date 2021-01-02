package sima.core.environment.event;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AgentTesting;

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
    public void cloneAndAddReceiverReturnsAnEventWhichIsTheSameEventWithAReceiverInPlus() {
        AgentTesting receiver = new AgentTesting("A_0", 0, null);
        Event e = EVENT.cloneAndAddReceiver(receiver.getAgentIdentifier());

        assertEquals(e.getSender(), EVENT.getSender());
        assertNotEquals(e.getReceiver(), EVENT.getReceiver());
        assertEquals(e.getProtocolTargeted(), EVENT.getProtocolTargeted());
    }

    @Test
    public void cloneAndAddReceiverThrowsExceptionIfTheEventNotSupportsClone() {
        AgentTesting a = new AgentTesting("TEST_A", 0, null);

        Event e = new Event(a.getAgentIdentifier(), null, null) {
            @Override
            protected Object clone() throws CloneNotSupportedException {
                throw new CloneNotSupportedException();
            }
        };

        AgentTesting receiver = new AgentTesting("A_1", 1, null);
        assertNull(e.cloneAndAddReceiver(receiver.getAgentIdentifier()));
    }

    @Test
    public void getSenderNeverReturnsNull() {
        assertNotNull(EVENT.getSender());
    }

    @Test
    public void isProtocolEventReturnsFalseIfProtocolTargetedIsNull() {
        this.verifyPreConditionAndExecuteTest(() -> NO_PROTOCOL_EVENT.getProtocolTargeted() == null,
                () -> assertFalse(NO_PROTOCOL_EVENT.isProtocolEvent()));
    }

    @Test
    public void isProtocolEventReturnsTrueIfProtocolTargetedIsNotNull() {
        this.verifyPreConditionAndExecuteTest(() -> PROTOCOL_EVENT.getProtocolTargeted() != null,
                () -> assertTrue(PROTOCOL_EVENT.isProtocolEvent()));
    }
}
