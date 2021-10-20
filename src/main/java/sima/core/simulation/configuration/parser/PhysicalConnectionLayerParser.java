package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.ConfigurationException;
import sima.core.exception.FailInstantiationException;
import sima.core.simulation.configuration.json.PhysicalConnectionLayerJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.HashMap;
import java.util.Map;

import static sima.core.utils.Utils.instantiate;
import static sima.core.utils.Utils.notNullOrThrows;

public class PhysicalConnectionLayerParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;
    private final Map<String, PhysicalConnectionLayerJson> mapPCL;

    // Constructors.

    public PhysicalConnectionLayerParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = notNullOrThrows(simaSimulationJson, new IllegalArgumentException("The simaSimulationJson cannot be null"));
        mapPCL = new HashMap<>();
    }

    // Methods.

    public void parsePhysicalConnectionLayers() throws ConfigurationException {
        mapPCL.clear();
        fillMapPCL();
    }

    private void fillMapPCL() throws ConfigurationException {
        if (simaSimulationJson.getPCLs() != null)
            for (PhysicalConnectionLayerJson pclJson : simaSimulationJson.getPCLs())
                mapPCL.put(notNullOrThrows(pclJson.getId(), new ConfigurationException("BehaviorId cannot be null")), pclJson);
    }

    /**
     * @return true if {@link #mapPCL} does not contain elements, else false.
     */
    public boolean isEmpty() {
        return mapPCL.isEmpty();
    }

    // Static.

    public static @NotNull PhysicalConnectionLayer instantiatePCL(
            Class<? extends PhysicalConnectionLayer> physicalConnectionLayerClass, Environment environment, Map<String, String> args)
            throws FailInstantiationException {
        return instantiate(physicalConnectionLayerClass, new Class[]{Environment.class, Map.class}, environment, args);
    }

    // Getters.

    public Map<String, PhysicalConnectionLayerJson> getMapPCL() {
        return mapPCL;
    }
}
