package sima.core.simulation.configuration.json;

import java.util.List;

public class EnvironmentJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private String name;
    private String environmentClass;
    private List<List<String>> args;

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
