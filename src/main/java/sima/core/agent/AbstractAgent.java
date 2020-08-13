package sima.core.agent;

import sima.core.agent.exception.AgentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.*;

public abstract class AbstractAgent {

    // Variables.

    private final String agentName;

    private final List<Environment> environments;

    private final List<Behavior> behaviors;

    private final Map<String, Protocol> mapProtocol;

    // Constructors.

    public AbstractAgent(String agentName, List<Class<? extends Behavior>> listBehaviors) throws AgentException {
        this.agentName = agentName;
        this.environments = new ArrayList<>();

        this.behaviors = new ArrayList<>();
        for (Class<? extends Behavior> behaviorClass : listBehaviors) {
            try {
                Constructor<? extends Behavior> constructor = behaviorClass.getConstructor(AbstractAgent.class);
                Behavior behavior = constructor.newInstance(this);
                this.behaviors.add(behavior);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                throw new AgentException(e);
            }
        }

        this.mapProtocol = new HashMap<>();
    }

    // Methods.

    public abstract void start();

    /**
     * @param protocolName the name of the protocol
     * @return the protocol associate to the protocolName, if no protocol is associated to its name, return null.
     */
    public Protocol getProtocol(String protocolName) {
        return this.mapProtocol.get(protocolName);
    }

    /**
     * Map the protocol name and the protocol together. If there was already a protocol mapped with the specified name,
     * the older protocol is removed and replace by the new specified protocol. The protocol name and the protocol can
     * not be null.
     *
     * @param protocolName the name of the protocol
     * @param protocol     the protocol, can not be null
     */
    public void addProtocol(String protocolName, Protocol protocol) {
        if (protocol != null && protocolName != null) {
            this.mapProtocol.put(protocolName, protocol);
        } else {
            throw new InvalidParameterException("Protocol name or protocol can not be null");
        }
    }

    /**
     * Unmap the protocol name with its protocol if there is a protocol mapped to this protocol name.
     *
     * @param protocolName the name of the protocol
     */
    public void removeProtocol(String protocolName) {
        if (protocolName != null) {
            this.mapProtocol.remove(protocolName);
        } else {
            throw new InvalidParameterException("Protocol name or protocol cannot be null");
        }
    }

    // Getters and Setters.

    public String getAgentName() {
        return agentName;
    }

    public List<Environment> getEnvironments() {
        return Collections.unmodifiableList(environments);
    }

    public List<Behavior> getBehaviors() {
        return Collections.unmodifiableList(this.behaviors);
    }

    public Map<String, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }
}
