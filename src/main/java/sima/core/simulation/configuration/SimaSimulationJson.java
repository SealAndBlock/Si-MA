package sima.core.simulation.configuration;

import java.util.List;

public class SimaSimulationJson {

    // Variables.

    private final int nbThreads;
    private final String timeMode;
    private final String schedulerType;
    private final String simulationSetupClass;
    private final List<EnvironmentJson> environments;
    private final List<ProtocolJson> protocols;
    private final List<BehaviorJson> behaviors;
    private final List<AgentJson> agents;
    private final List<List<String>> args;

    // Constructors.

    public SimaSimulationJson(int nbThreads, String timeMode, String schedulerType, String simulationSetupClass,
                              List<EnvironmentJson> environments, List<ProtocolJson> protocols, List<BehaviorJson> behaviors,
                              List<AgentJson> agents, List<List<String>> args) {
        this.nbThreads = nbThreads;
        this.timeMode = timeMode;
        this.schedulerType = schedulerType;
        this.simulationSetupClass = simulationSetupClass;
        this.environments = environments;
        this.protocols = protocols;
        this.behaviors = behaviors;
        this.agents = agents;
        this.args = args;
    }

    // Methods.

    @Override
    public String toString() {
        return "SimaSimulationJSON{" +
                "nbThreads=" + nbThreads +
                ", timeMode='" + timeMode + '\'' +
                ", schedulerType='" + schedulerType + '\'' +
                ", simulationSetupClass='" + simulationSetupClass + '\'' +
                ", environments=" + environments +
                ", protocols=" + protocols +
                ", behaviors=" + behaviors +
                ", agents=" + agents +
                ", args=" + args +
                '}';
    }

    // Getters and Setters.

    public int getNbThreads() {
        return nbThreads;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public String getSchedulerType() {
        return schedulerType;
    }

    public String getSimulationSetupClass() {
        return simulationSetupClass;
    }

    public List<EnvironmentJson> getEnvironments() {
        return environments;
    }

    public List<ProtocolJson> getProtocols() {
        return protocols;
    }

    public List<BehaviorJson> getBehaviors() {
        return behaviors;
    }

    public List<AgentJson> getAgents() {
        return agents;
    }

    public List<List<String>> getArgs() {
        return args;
    }
}
