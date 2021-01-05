package sima.core.scheduler;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestControllerTesting extends GlobalTestController {

    // Initialization.

    @Override
    protected void verifyAndSetup() {
        CONTROLLER = new ControllerTesting(null);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructControllerWithNotNullArgsCallProcessArgument() {
        ControllerTesting controllerTesting = new ControllerTesting(new HashMap<>());
        assertEquals(1, controllerTesting.getPassProcessArgument());
    }

}
