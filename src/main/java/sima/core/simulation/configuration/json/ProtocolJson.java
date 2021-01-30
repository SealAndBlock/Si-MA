package sima.core.simulation.configuration.json;


import sima.core.protocol.ProtocolIdentifier;
import sima.core.utils.Utils;

import java.util.List;
import java.util.Map;

public class ProtocolJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private String tag;
    private String protocolClass;
    private Map<String, String> protocolDependencies;
    private List<List<String>> args;

    // Methods.

    public ProtocolIdentifier extractProtocolIdentifier() throws ClassNotFoundException {
        return new ProtocolIdentifier(Utils.extractClassForName(protocolClass), tag);
    }

    // Getters.

    public String getTag() {
        return tag;
    }

    public String getProtocolClass() {
        return protocolClass;
    }

    public Map<String, String> getProtocolDependencies() {
        return protocolDependencies;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
