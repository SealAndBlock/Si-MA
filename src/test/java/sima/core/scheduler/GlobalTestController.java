package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public abstract class GlobalTestController extends GlobalTestExecutable {

    // Static.

    protected static Controller CONTROLLER;

    // Initialization.

    @Override
    protected void verifyAndSetup() {
        EXECUTABLE = CONTROLLER;

        assertNotNull(CONTROLLER, "CONTROLLER cannot be null for tests");

        super.verifyAndSetup();
    }
}