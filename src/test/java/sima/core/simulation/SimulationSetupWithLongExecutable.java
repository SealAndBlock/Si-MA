package sima.core.simulation;

import sima.core.scheduler.LongTimeExecutableTesting;
import sima.core.scheduler.Scheduler;

import java.util.Map;

public class SimulationSetupWithLongExecutable extends SimulationSetup {

    // Constructors.

    public SimulationSetupWithLongExecutable(Map<String, String> dummy) {
        super(dummy);
    }

    // Methods.

    @Override
    public void setupSimulation() {
        SimaSimulation.getScheduler().scheduleExecutableOnce(new LongTimeExecutableTesting(), Scheduler.NOW);
    }
}
