package sima.core.simulation.configuration.json;

public class ObjectIdJson {

    // Variables.

    private final String id;

    // Constructors.

    public ObjectIdJson(String id) {
        this.id = id;
    }

    // Methods.

    @Override
    public String toString() {
        return "ObjectIdJson {" +
                "id='" + id + '\'' +
                '}';
    }

    // Getters.

    public String getId() {
        return id;
    }
}
