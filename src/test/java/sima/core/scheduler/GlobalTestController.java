package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
abstract class GlobalTestController extends GlobalTestExecutable {
    
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
}
