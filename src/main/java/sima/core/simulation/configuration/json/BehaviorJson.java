package sima.core.simulation.configuration.json;

import java.util.List;

public class BehaviorJson extends ObjectIdJson implements ArgumentativeObjectJson {

    // Variables.

    private final String behaviorClass;
    private final List<List<String>> args;

    // Constructors.

    public BehaviorJson(String id, String behaviorClass, List<List<String>> args) {
        super(id);
        this.behaviorClass = behaviorClass;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "BehaviorJson {" +
                "id=" + getId() +
                ", behaviorClass='" + behaviorClass + '\'' +
                ", args=" + args +
                '}';
    }

    // Getters.

    public String getBehaviorClass() {
        return behaviorClass;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
