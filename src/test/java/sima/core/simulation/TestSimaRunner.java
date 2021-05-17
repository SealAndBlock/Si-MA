package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sima.core.simulation.TestSimaSimulation.PREFIX_CONFIG_PATH;


class TestSimaRunner extends SimaTest {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        
    }
    
    // Tests.
    
    @Test
    void launchWithEmptyArgsNotFail() {
        assertThrows(IllegalArgumentException.class, () -> SimaRunner.main(new String[]{}));
    }
    
    @Test
    void launchWithOneArgsNotFail() {
        assertDoesNotThrow(() -> SimaRunner.main(new String[]{PREFIX_CONFIG_PATH + "fullConfig.json"}));
    }
}
