package sima.core.simulation.configuration.json;

import java.util.List;

public class SimaSimulationJson {

    // Variables.

    private long endTime;
    private int nbThreads;
    private String timeMode;
    private String schedulerType;
    private String simulationSetupClass;
    private String schedulerWatcherClass;
    private String simaWatcherClass;
    private List<ControllerJson> controllers;
    private List<EnvironmentJson> environments;
    private List<ProtocolJson> protocols;
    private List<BehaviorJson> behaviors;
    private List<AgentJson> agents;

    // Getters.

    public long getEndTime() {
        return endTime;
    }

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

    public String getSchedulerWatcherClass() {
        return schedulerWatcherClass;
    }

    public String getSimaWatcherClass() {
        return simaWatcherClass;
    }

    public List<ControllerJson> getControllers() {
        return controllers;
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
}
