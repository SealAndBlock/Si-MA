package sima.core.agent.basic;

import org.junit.jupiter.api.Test;
import sima.basic.agent.SimpleAgent;
import sima.core.agent.GlobalTestAbstractAgent;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimpleAgent extends GlobalTestAbstractAgent {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AGENT_0 = new SimpleAgent("AGENT_0", 0, 0, null);
        AGENT_1 = new SimpleAgent("AGENT_1", 1, 1, null);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructSimpleAgentWithNullNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new SimpleAgent(null, 0, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNotNullNameNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNegativeSequenceIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("AGENT", -1, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithPositiveSequenceIdNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNegativeUniqueIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("AGENT", 0, -1, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithPositiveUniqueIdNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, null));
    }

    @Test
    public void constructSimpleAgentWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, 0, new HashMap<>()));
    }
}
