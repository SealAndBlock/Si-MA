package sima.core.simulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimaSimulation {

    @Test
    public void testSimulationIsRunning() {
        // Simulation not running.
        assertFalse(SimaSimulation.simulationIsRunning());

        // Test if all methods throw NullPointerException.
        assertThrows(NullPointerException.class, SimaSimulation::getScheduler);
        assertThrows(NullPointerException.class, SimaSimulation::currentTime);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getAgentFromIdentifier(null));
        assertThrows(NullPointerException.class, SimaSimulation::getAllEnvironments);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getEnvironmentFromName(null));
        assertThrows(NullPointerException.class, SimaSimulation::timeMode);
    }

}
