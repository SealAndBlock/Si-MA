package sima.core.scheduler;

public class TestControllerTesting extends GlobalTestController {

    // Initialization.

    @Override
    protected void verifyAndSetup() {
        CONTROLLER = new ControllerTesting(null);

        super.verifyAndSetup();
    }

    // Tests.

}
