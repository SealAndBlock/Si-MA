package sima.core.simulation.configuration;

public class ObjectIdJSON {

    // Variables.

    private final String id;

    // Constructors.

    public ObjectIdJSON(String id) {
        this.id = id;
    }

    // Methods.

    @Override
    public String toString() {
        return "ObjectIdJSON{" +
                "id='" + id + '\'' +
                '}';
    }

    // Getters.

    public String getId() {
        return id;
    }
}
