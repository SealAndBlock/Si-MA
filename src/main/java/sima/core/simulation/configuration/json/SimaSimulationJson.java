package sima.core.simulation.configuration.json;

import sima.core.agent.AbstractAgent;
import sima.core.environment.Environment;
import sima.core.protocol.ProtocolIdentifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Object> mapIdObject;

    // Methods.

    public void linkIdAndObject(String id, Object object) {
        if (mapIdObject == null) {
            mapIdObject = new HashMap<>();
        }

        mapIdObject.put(id, object);
    }

    public Object getInstanceFromId(String id, AbstractAgent agent) {
        var object = mapIdObject.get(id);
        if (object != null) {
            if (object instanceof ProtocolIdentifier) {
                return agent.getProtocol((ProtocolIdentifier) object);
            } else if (object instanceof Environment) {
                return object;
            } else {
                throw new UnsupportedOperationException("Object link to the id cannot be supported");
            }
        } else
            throw new IllegalArgumentException("Id not found. The id : " + id);
    }

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
