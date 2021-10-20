package sima.core.simulation.configuration.json;

import java.util.List;

public class AgentJson implements ArgumentativeObjectJson {

    // Variables.

    private String agentClass;
    private String namePattern;
    private int numberToCreate;
    private List<String> behaviors;
    private List<String> protocols;
    private List<String> environments;
    private List<List<String>> args;

    // Methods.

    /**
     * @return true if {@link #environments} is not null and not empty.
     */
    public boolean hasEnvironment() {
        return environments != null && !environments.isEmpty();
    }

    /**
     * @return true if {@link #behaviors} is not null and not empty.
     */
    public boolean hasBehavior() {
        return behaviors != null && !behaviors.isEmpty();
    }

    // Getters.

    public String getAgentClass() {
        return agentClass;
    }

    public String getNamePattern() {
        return namePattern;
    }

    public int getNumberToCreate() {
        return numberToCreate;
    }

    public List<String> getBehaviors() {
        return behaviors;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
