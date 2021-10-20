package sima.core.simulation.configuration.parser;

import sima.core.agent.SimaAgent;
import sima.core.exception.ConfigurationException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.simulation.configuration.json.ProtocolJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.notNullOrThrows;

public class ProtocolParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;
    private final Map<String, ProtocolJson> mapProtocols;

    // Constructors.

    public ProtocolParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = notNullOrThrows(simaSimulationJson, new IllegalArgumentException("The simaSimulationJson cannot be null"));
        mapProtocols = new HashMap<>();
    }

    // Methods.

    /**
     * Maps the id of the protocols specified in the configuration with the {@link ProtocolParser} which contains all construction information to
     * construct the {@link Protocol} in {@link SimaAgent} which will use it.
     * <p>
     * After the call of this method. The {@link #mapProtocols} is filled, and then you can use it with the getter {@link #getMapProtocols()}.
     * <p>
     * Each call of this method clear the {@link #mapProtocols} and then it is re filled.
     */
    public void parseProtocols() throws ConfigurationException, ClassNotFoundException {
        mapProtocols.clear();
        fillMapProtocols();
    }

    private void fillMapProtocols() throws ConfigurationException, ClassNotFoundException {
        if (simaSimulationJson.getProtocols() != null)
            for (ProtocolJson protocolJson : simaSimulationJson.getProtocols()) {
                mapProtocols.put(Optional.ofNullable(protocolJson.getId()).orElseThrow(() -> new ConfigurationException("ProtocolId cannot be null")),
                                 protocolJson);
                simaSimulationJson.linkIdAndObject(protocolJson.getId(), new ProtocolIdentifier(extractClassForName(protocolJson.getProtocolClass()),
                                                                                                protocolJson.getTag()));
            }
    }

    // Getters.

    public Map<String, ProtocolJson> getMapProtocols() {
        return mapProtocols;
    }
}
