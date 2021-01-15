package sima.core.simulation.configuration;

import java.util.List;

public class SimaSimulationJSON {

    // Variables.

    private final int nbThreads;
    private final String timeMode;
    private final String schedulerType;
    private final String simulationSetupClass;
    private final List<EnvironmentJSON> environments;
    private final List<ProtocolJSON> protocols;
    private final List<BehaviorJSON> behaviors;
    private final List<AgentJSON> agents;
    private final List<List<String>> args;

    // Constructors.

    public SimaSimulationJSON(int nbThreads, String timeMode, String schedulerType, String simulationSetupClass,
                              List<EnvironmentJSON> environments, List<ProtocolJSON> protocols, List<BehaviorJSON> behaviors,
                              List<AgentJSON> agents, List<List<String>> args) {
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

    public List<EnvironmentJSON> getEnvironments() {
        return environments;
    }

    public List<ProtocolJSON> getProtocols() {
        return protocols;
    }

    public List<BehaviorJSON> getBehaviors() {
        return behaviors;
    }

    public List<AgentJSON> getAgents() {
        return agents;
    }

    public List<List<String>> getArgs() {
        return args;
    }
}
