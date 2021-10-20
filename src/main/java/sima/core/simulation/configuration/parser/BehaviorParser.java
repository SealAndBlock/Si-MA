package sima.core.simulation.configuration.parser;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.exception.ConfigurationException;
import sima.core.simulation.configuration.json.BehaviorJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static sima.core.utils.Utils.notNullOrThrows;

public class BehaviorParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;
    private final Map<String, BehaviorJson> mapBehaviors;

    // Constructors.

    public BehaviorParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = notNullOrThrows(simaSimulationJson, new IllegalArgumentException("The simaSimulationJson cannot be null"));
        mapBehaviors = new HashMap<>();
    }

    // Methods.

    /**
     * Maps the id of the behavior specified in the configuration with the {@link BehaviorJson} which contains all construction information to
     * construct the {@link Behavior} in {@link SimaAgent} which will use it.
     * <p>
     * After the call of this method. The {@link #mapBehaviors} is filled, and then you can use it with the getter {@link #getMapBehaviors()}.
     * <p>
     * Each call of this method clear the {@link #mapBehaviors} and then it is re filled.
     */
    public void parseBehaviors() throws ConfigurationException {
        mapBehaviors.clear();
        fillMapBehaviors();
    }

    private void fillMapBehaviors() throws ConfigurationException {
        if (simaSimulationJson.getBehaviors() != null)
            for (BehaviorJson behaviorJson : simaSimulationJson.getBehaviors()) {
                mapBehaviors.put(Optional.ofNullable(behaviorJson.getId()).orElseThrow(() -> new ConfigurationException("BehaviorId cannot be null")),
                                 behaviorJson);
            }
    }

    // Getters.

    public Map<String, BehaviorJson> getMapBehaviors() {
        return mapBehaviors;
    }
}
