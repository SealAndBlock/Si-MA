package sima.core.simulation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class TestSimulationSetupTesting extends GlobalTestSimulationSetup {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        SIMULATION_SETUP = new SimulationSetupTesting(null);

        super.verifyAndSetup();
    }

    // Test.

    @Test
    public void constructSimulationSetupTestingWithNullArgsNotFail() {
        notFail(() -> new SimulationSetupTesting(null));
    }

    @Test
    public void constructSimulationSetupTestingWithNotNullArgsNotFail() {
        notFail(() -> new SimulationSetupTesting(new HashMap<>()));
    }
}
