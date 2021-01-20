package sima.core.simulation.configuration;

import java.util.List;

public class BehaviorJson extends JsonObjectId implements ArgumentativeJsonObject {

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
        return "BehaviorJSON{" +
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
