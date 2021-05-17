package sima.core.simulation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestSimulationSetupTesting extends GlobalTestSimulationSetup {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        SIMULATION_SETUP = new SimulationSetupTesting(null);
        
        super.verifyAndSetup();
    }
    
    // Test.
    
    @Test
    void constructSimulationSetupTestingWithNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimulationSetupTesting(null));
    }
    
    @Test
    void constructSimulationSetupTestingWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new SimulationSetupTesting(new HashMap<>()));
    }
}
