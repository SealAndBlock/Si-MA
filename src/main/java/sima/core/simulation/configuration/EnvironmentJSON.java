package sima.core.simulation.configuration;

import java.util.List;

public class EnvironmentJSON extends ObjectIdJSON {

    // Variables.

    private final String name;
    private final String environmentClass;
    private final List<List<String>> args;

    // Constructors.

    public EnvironmentJSON(String id, String name, String environmentClass, List<List<String>> args) {
        super(id);
        this.name = name;
        this.environmentClass = environmentClass;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "EnvironmentJSON{" +
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

    public List<List<String>> getArgs() {
        return args;
    }
}
