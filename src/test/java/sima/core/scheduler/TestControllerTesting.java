package sima.core.scheduler;

class TestControllerTesting extends GlobalTestController {
    
    // Initialization.
    
    @Override
    protected void verifyAndSetup() {
        CONTROLLER = new ControllerTesting(null);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
}
