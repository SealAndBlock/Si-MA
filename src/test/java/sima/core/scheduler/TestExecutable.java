package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.TestInitializer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public abstract class TestExecutable extends TestInitializer {

    // Static.

    protected static Executable EXECUTABLE;

    // Initialization.

    @Override
    protected void initialize() {
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
