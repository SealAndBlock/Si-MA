package sima.core.simulation;

public class SimaWatcherTesting implements SimaSimulation.SimaWatcher {

    // Variables.

    private int passToOnSimStarted;
    private int passToInSimKilled;

    // Constructors.

    public SimaWatcherTesting() {
        this.passToOnSimStarted = 0;
        this.passToInSimKilled = 0;
    }

    // Methods.

    @Override
    public void notifyOnSimulationStarted() {
        passToOnSimStarted++;
    }

    @Override
    public void notifyOnSimulationKilled() {
        passToInSimKilled++;
    }

    // Getters and Setters.

    public int getPassToOnSimStarted() {
        return passToOnSimStarted;
    }

    public int getPassToInSimKilled() {
        return passToInSimKilled;
    }
}
