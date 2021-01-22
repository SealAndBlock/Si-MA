package sima.core.simulation.configuration.json;

import java.util.List;

public class BehaviorJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private String behaviorClass;
    private List<List<String>> args;

    // Getters.

    public String getBehaviorClass() {
        return behaviorClass;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
