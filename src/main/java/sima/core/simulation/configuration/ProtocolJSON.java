package sima.core.simulation.configuration;


import java.util.List;

public class ProtocolJSON extends ObjectIdJSON {

    // Variables.

    private final String tag;
    private final String protocolClass;
    private final List<List<String>> args;

    // Constructors.

    public ProtocolJSON(String id, String tag, String protocolClass, List<List<String>> args) {
        super(id);
        this.tag = tag;
        this.protocolClass = protocolClass;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "ProtocolJSON{" +
                "tag='" + tag + '\'' +
                ", protocolClass='" + protocolClass + '\'' +
                ", args=" + args +
                '}';
    }

    // Getters.

    public String getTag() {
        return tag;
    }

    public String getProtocolClass() {
        return protocolClass;
    }

    public List<List<String>> getArgs() {
        return args;
    }
}
