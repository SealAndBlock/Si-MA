package sima.core.simulation.configuration;

import java.util.List;

public class AgentJSON {

    // Variables.

    private final String agentClass;
    private final String namePattern;
    private final int numberToCreate;
    private final List<String> behaviors;
    private final List<String> protocols;
    private final List<String> environments;
    private final List<List<String>> args;

    // Constructors.

    public AgentJSON(String agentClass, String namePattern, int numberToCreate, List<String> behaviors,
                     List<String> protocols, List<String> environments, List<List<String>> args) {
        this.agentClass = agentClass;
        this.namePattern = namePattern;
        this.numberToCreate = numberToCreate;
        this.behaviors = behaviors;
        this.protocols = protocols;
        this.environments = environments;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "AgentJSON{" +
                "agentClass='" + agentClass + '\'' +
                ", namePattern='" + namePattern + '\'' +
                ", numberToCreate=" + numberToCreate +
                ", behaviors=" + behaviors +
                ", protocols=" + protocols +
                ", environments=" + environments +
                ", args=" + args +
                '}';
    }

    // Getters and Setters.

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

    public List<List<String>> getArgs() {
        return args;
    }
}
