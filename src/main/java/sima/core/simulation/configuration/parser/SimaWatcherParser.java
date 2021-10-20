package sima.core.simulation.configuration.parser;

import sima.core.exception.FailInstantiationException;
import sima.core.simulation.SimaSimulation;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.instantiate;

public class SimaWatcherParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;

    private SimaSimulation.SimaWatcher simaWatcher;

    // Constructors.

    public SimaWatcherParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = simaSimulationJson;
        simaWatcher = null;
    }

    // Methods.

    public void parseSimaWatcher() throws ClassNotFoundException, FailInstantiationException {
        simaWatcher = null;
        if (simaSimulationJson.hasSimaWatcher())
            simaWatcher = instantiateSimaWatcher(extractClassForName(simaSimulationJson.getSimaWatcherClass()));
    }

    // Static.

    private SimaSimulation.SimaWatcher instantiateSimaWatcher(Class<? extends SimaSimulation.SimaWatcher> simaWatcherClass)
            throws FailInstantiationException {
        return instantiate(simaWatcherClass);
    }

    // Getters.

    public SimaSimulation.SimaWatcher getSimaWatcher() {
        return simaWatcher;
    }
}
