package sima.core.simulation;

import java.util.Map;

public class SimulationSetupTesting extends SimulationSetup {

    // Variables.

    private int passToSetupSimulation;

    // Constructors.

    public SimulationSetupTesting(Map<String, String> dummy) {
        super(dummy);
        passToSetupSimulation = 0;
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
    }

    @Override
    public void setupSimulation() {
        passToSetupSimulation++;
    }

    // Getters and Setters.

    public int getPassToSetupSimulation() {
        return passToSetupSimulation;
    }
}
