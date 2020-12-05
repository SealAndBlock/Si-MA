package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public class TestAbstractAgent extends SimaTest {

    // Static.

    protected static AbstractAgent AGENT;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT, "AGENT cannot be null for tests");
    }
}
