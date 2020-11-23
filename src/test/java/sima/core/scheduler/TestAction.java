package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public abstract class TestAction extends TestExecutable {

    // Static.

    protected static Action ACTION;

    // Initialization.

    @Override
    protected void verifyAndSetup() {
        EXECUTABLE = ACTION;

        assertNotNull(ACTION, "ACTION cannot be null for the tests");

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void getExecutorAgentNotThrowsException() {
        try {
            ACTION.getExecutorAgent();
        } catch (Exception e) {
            fail(e);
        }
    }

}
