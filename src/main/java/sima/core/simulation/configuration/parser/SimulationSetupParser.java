package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.FailInstantiationException;
import sima.core.simulation.SimulationSetup;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.Map;

import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.instantiate;

public class SimulationSetupParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;

    private SimulationSetup simulationSetup;

    // Constructors.

    public SimulationSetupParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = simaSimulationJson;
        simulationSetup = null;
    }

    // Methods.

    public void parseSimulationSetup() throws ClassNotFoundException, FailInstantiationException {
        simulationSetup = null;
        if (simaSimulationJson.hasSimulationSetup())
            simulationSetup = instantiateSimulationSetup(extractClassForName(simaSimulationJson.getSimulationSetupClass()));
    }

    // Static.

    /**
     * Try to create a new instance of the {@link SimulationSetup} specified class.
     *
     * @param simulationSetupClass the class of the SimulationSetup
     *
     * @return a new instance of the {@link SimulationSetup} specified class. If the instantiation failed, throws a {@link
     * FailInstantiationException}.
     *
     * @throws FailInstantiationException if the instantiation fail
     */
    public static @NotNull SimulationSetup instantiateSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass)
            throws FailInstantiationException {
        return instantiate(simulationSetupClass, new Class[]{Map.class}, (Map<String, String>) null);
    }

    // Getters.

    public SimulationSetup getSimulationSetup() {
        return simulationSetup;
    }
}
