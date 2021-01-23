package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sima.core.simulation.SimaSimulationTest.PREFIX_CONFIG_PATH;


public class TestSimaRunner extends SimaTest {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {

    }

    // Tests.

    @Test
    public void launchWithNullArgsThrowsException() {
        assertThrows(NullPointerException.class, () -> SimaRunner.main(null));
    }

    @Test
    public void launchWithEmptyArgsNotFail() {
        assertThrows(IllegalArgumentException.class, () -> SimaRunner.main(new String[]{}));
    }

    @Test
    public void launchWithOneArgsNotFail() {
        assertDoesNotThrow(() -> SimaRunner.main(new String[]{PREFIX_CONFIG_PATH + "fullConfig.json"}));
    }
}
