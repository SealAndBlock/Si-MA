package sima.core.simulation.configuration.json;

import sima.core.agent.SimaAgent;
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
    private List<PhysicalConnectionLayerJson> physicalConnectionLayers;
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

    public Object getInstanceFromId(String id, SimaAgent agent) {
        var object = mapIdObject.get(id);
        if (object != null) {
            if (object instanceof ProtocolIdentifier protocolIdentifier) {
                return agent.getProtocol(protocolIdentifier);
            } else if (object instanceof Environment) {
                return object;
            } else {
                throw new UnsupportedOperationException("Object link to the id cannot be supported");
            }
        } else
            throw new IllegalArgumentException("Id not found. The id : " + id);
    }

    /**
     * @return true if {@link #simaWatcherClass} is not null and not empty.
     */
    public boolean hasSimaWatcher() {
        return simaWatcherClass != null && !simaWatcherClass.isEmpty();
    }

    /**
     * @return true if {@link #controllers} is not null and not empty.
     */
    public boolean hasControllers() {
        return controllers != null && !controllers.isEmpty();
    }

    /**
     * @return true if {@link #simulationSetupClass} is not null and not empty string.
     */
    public boolean hasSimulationSetup() {
        return simulationSetupClass != null && !simulationSetupClass.isEmpty();
    }

    /**
     * @return true if the list of agent is not null and not empty.
     */
    public boolean hasAgents() {
        return agents != null && !agents.isEmpty();
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

    public List<PhysicalConnectionLayerJson> getPCLs() {
        return physicalConnectionLayers;
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
