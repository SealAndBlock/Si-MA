package sima.core.simulation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
abstract class GlobalTestSimulationSetup extends SimaTest {
    
    // Static.
    
    protected SimulationSetup SIMULATION_SETUP;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(SIMULATION_SETUP, "SIMULATION_SETUP cannot be null for tests");
    }
    
    // Tests.
    
    @Test
    void setupSimulationNotFail() {
        assertDoesNotThrow(SIMULATION_SETUP::setupSimulation);
    }
}
