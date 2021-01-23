package sima.core.simulation.specific;

import sima.core.simulation.SimulationSetupTesting;

import java.util.Map;

public class SpecificSimulationSetupTesting extends SimulationSetupTesting {

    // Static.

    public static int PASS_SETUP_SIMULATION = 0;

    // Constructors.

    public SpecificSimulationSetupTesting(Map<String, String> dummy) {
        super(dummy);
    }

    // Methods.

    @Override
    public void setupSimulation() {
        super.setupSimulation();
        PASS_SETUP_SIMULATION++;
    }
}
