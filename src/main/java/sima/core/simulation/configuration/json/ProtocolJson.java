package sima.core.simulation.configuration.json;


import java.util.List;

public class ProtocolJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private String tag;
    private String protocolClass;
    private List<List<String>> args;

    // Getters.

    public String getTag() {
        return tag;
    }

    public String getProtocolClass() {
        return protocolClass;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
