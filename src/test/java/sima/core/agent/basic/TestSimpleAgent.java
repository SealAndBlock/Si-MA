package sima.core.agent.basic;

import org.junit.jupiter.api.Test;
import sima.core.agent.GlobalTestAbstractAgent;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimpleAgent extends GlobalTestAbstractAgent {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AGENT_0 = new SimpleAgent("AGENT_0", 0, null);
        AGENT_1 = new SimpleAgent("AGENT_1", 1, null);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructSimpleAgentWithNullNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new SimpleAgent(null, 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNotNullNameNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("AGENT", -1, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithPositiveNumberNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, new HashMap<>()));
    }

    @Test
    public void constructSimpleAgentWithNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, null));
    }

    @Test
    public void constructSimpleAgentWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimpleAgent("AGENT", 0, new HashMap<>()));
    }
}
