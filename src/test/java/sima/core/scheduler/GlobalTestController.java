package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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

    // Tests.

    @Test
    public void constructControllerWithNullArgsNotFail() {
        testNotFail(() -> new Controller(null) {

            @Override
            public void execute() {
            }

            @Override
            protected void processArgument(Map<String, String> args) {
            }
        });
    }

    @Test
    public void constructControllerWithNotNullArgsNotFail() {
        testNotFail(() -> new Controller(new HashMap<>()) {

            @Override
            public void execute() {
            }

            @Override
            protected void processArgument(Map<String, String> args) {
            }
        });
    }
}
