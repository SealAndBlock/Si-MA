package sima.core.simulation.specific;

import sima.core.scheduler.LongTimeExecutableTesting;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.SimaSimulation;
import sima.core.simulation.SimulationSetup;

import java.util.Map;

public class SpecificSimulationSetupWithLongExecutable implements SimulationSetup {
    
    // Constructors.
    
    public SpecificSimulationSetupWithLongExecutable(Map<String, String> dummy) {
    }
    
    // Methods.
    
    @Override
    public void setupSimulation() {
        SimaSimulation.getScheduler().scheduleExecutableOnce(new LongTimeExecutableTesting(), Scheduler.NOW);
    }
}
