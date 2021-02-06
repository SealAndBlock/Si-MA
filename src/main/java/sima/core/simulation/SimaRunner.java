package sima.core.simulation;

import sima.core.exception.SimaSimulationFailToStartRunningException;

import static sima.core.simulation.SimaSimulation.SIMA_LOG;

public class SimaRunner {

    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new IllegalArgumentException("The simulation must have in argument the Json Configuration File");

            SimaSimulation.runSimulation(args[0]);
        } catch (SimaSimulationFailToStartRunningException e) {
            SIMA_LOG.error("Fail to run SimaSimulation.", e);
        }

        SimaSimulation.waitEndSimulation();
    }

}
