package sima.core.simulation.configuration.json;

import java.util.List;

public class EnvironmentJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private final String name;
    private final String environmentClass;
    private final List<List<String>> args;

    // Constructors.

    public EnvironmentJson(String id, String name, String environmentClass, List<List<String>> args) {
        super(id);
        this.name = name;
        this.environmentClass = environmentClass;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "EnvironmentJson {" +
                "name='" + name + '\'' +
                ", environmentClass='" + environmentClass + '\'' +
                ", args=" + args +
                '}';
    }

    // Getters.

    public String getName() {
        return name;
    }

    public String getEnvironmentClass() {
        return environmentClass;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
