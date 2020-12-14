package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public abstract class GlobalTestExecutable extends SimaTest {

    // Static.

    protected static Executable EXECUTABLE;

    // Initialization.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(EXECUTABLE, "EXECUTABLE cannot be null for the tests");
    }

    // Tests.

    @Test
    public void executeNotThrowsException() {
        try {
            EXECUTABLE.execute();
        } catch (Exception e) {
            fail(e);
        }
    }
}
