package sima.core.agent;

import sima.core.agent.exception.AgentException;
import sima.core.environment.Environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.*;

public abstract class AbstractAgent {

    // Variables.

    /**
     * The name of the agent
     */
    private final String agentName;

    /**
     * The several environments where the agent evolves.
     * <p>
     * Associate the name of the class of the environment and the instance of the environment.
     */
    private final Map<String, Environment> environments;

    /**
     * The several behaviors that the agent can have.
     * <p>
     * Associate the name of the class of the behavior and the instance of the behavior.
     */
    private final Map<String, Behavior> mapBehaviors;

    /**
     * The several protocols that the agent can use.
     * <p>
     * Associate the name of the class of the protocol and the instance of the protocol.
     */
    private final Map<String, Protocol> mapProtocol;

    // Constructors.

    public AbstractAgent(String agentName, List<Environment> environments,
                         List<Class<? extends Behavior>> listBehaviors) throws AgentException {
        this.agentName = agentName;

        this.environments = new HashMap<>();
        for (Environment environment : environments) {
            this.environments.put(environment.getName(), environment);
        }

        this.mapBehaviors = new HashMap<>();
        for (Class<? extends Behavior> behaviorClass : listBehaviors) {
            try {
                Constructor<? extends Behavior> constructor = behaviorClass.getConstructor(AbstractAgent.class);
                Behavior behavior = constructor.newInstance(this);
                this.mapBehaviors.put(behaviorClass.getName(), behavior);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                throw new AgentException(e);
            }
        }

        this.mapProtocol = new HashMap<>();
    }

    // Methods.

    /**
     * Start the agent.
     */
    public void start() {
        // TODO make the start method.
    }

    public abstract void onStart();

    /**
     * Kill the agent. When an agent is killed, it cannot be restarted.
     */
    public void kill() {
        // TODO make the kill method.
    }

    public abstract void onKill();

    /**
     * @param environmentName the environment name
     * @return true if the environment name is mapped to an environment in the agent.
     */
    public boolean isEvolvingInEnvironment(String environmentName) {
        return this.environments.containsKey(environmentName);
    }

    /**
     * Remove the environment of the agent.
     *
     * @param environmentName the environment name
     */
    public void leaveEnvironment(String environmentName) {
        this.environments.remove(environmentName);
    }

    /**
     * Search if the behavior is a behavior of the agent, if it is the case, call the method
     * {@link Behavior#startPlaying()}.
     *
     * @param behaviorClass the class of the behavior that we want starting to play
     */
    public void startPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            behavior.startPlaying();
    }

    /**
     * Search if the behavior is a behavior of the agent, if it is the case, call the method
     * {@link Behavior#stopPlaying()}.
     *
     * @param behaviorClass the class of the behavior that we want stopping to play
     */
    public void stopPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            behavior.stopPlaying();
    }

    /**
     * @param behaviorClass the class of the behavior
     * @return true if the agent can play the specified behavior, else false.
     */
    public boolean canPlayBehavior(Class<? extends Behavior> behaviorClass) {
        return this.mapBehaviors.containsKey(behaviorClass.getName());
    }

    /**
     * Verifies if the behaviors can be played by the agent with the method{@link #canPlayBehavior(Class)} and if it the
     * case, look if the behavior is playing by the agent by calling the function {@link Behavior#isPlaying()}
     *
     * @param behaviorClass the class of the behavior
     * @return true if the specified behavior is playing by the agent, else false.
     */
    public boolean isPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        if (this.canPlayBehavior(behaviorClass)) {
            return this.mapBehaviors.get(behaviorClass.getName()).isPlaying();
        } else
            return false;
    }

    /**
     * @param protocolName the name of the protocol
     * @return the protocol associate to the protocolName, if no protocol is associated to its name, return null.
     */
    public Protocol getProtocol(String protocolName) {
        return this.mapProtocol.get(protocolName);
    }

    /**
     * Map the protocol name and the protocol together. If there was already a protocol mapped with the specified name,
     * the older protocol is removed and replace by the new specified protocol. The protocolName and the protocol cannot
     * be null.
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

    public Map<String, Environment> getEnvironments() {
        return Collections.unmodifiableMap(environments);
    }

    public Map<String, Behavior> getMapBehaviors() {
        return Collections.unmodifiableMap(this.mapBehaviors);
    }

    public Map<String, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }
}
