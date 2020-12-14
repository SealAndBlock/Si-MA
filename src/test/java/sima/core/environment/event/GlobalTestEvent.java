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

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(EVENT, "EVENT cannot be null for tests");
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
    public void getSenderNeverReturnsNull() {
        assertNotNull(EVENT.getSender());
    }
}
