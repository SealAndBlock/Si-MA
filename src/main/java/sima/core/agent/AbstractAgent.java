package sima.core.agent;

import sima.core.agent.exception.AgentException;
import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
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
     * Associate the name of the environment name and the instance of the environment.
     */
    private final Map<String, Environment> mapEnvironments;

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

    /**
     * True if the agent is started, else false.
     */
    private boolean isStarted = false;

    /**
     * True if the agent is killed, else false. If an agent is killed, it stops to be started and cannot become
     * started again.
     */
    private boolean isKilled = false;

    // Constructors.

    /**
     * Constructs an agent with a name, a list of environments and a list of all behaviors the agent can play.
     *
     * @param agentName     the agent name
     * @param environments  the list of environment where the agent evolves
     * @param listBehaviors the list of behaviors that the agent can play
     * @throws AgentException when the agent cannot instantiate a class of a behavior
     */
    public AbstractAgent(String agentName, List<Environment> environments,
                         List<Class<? extends Behavior>> listBehaviors) throws AgentException {
        this.agentName = agentName;

        this.mapEnvironments = new HashMap<>();
        for (Environment environment : environments) {
            this.joinEnvironment(environment);
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
     * <p>
     * When an agent is started, it calls the method {@link #onStart()}.
     *
     * @throws KilledAgentException         if the agent is killed
     * @throws AlreadyStartedAgentException if the agent have already been started
     */
    public void start() throws KilledAgentException, AlreadyStartedAgentException {
        if (!this.isKilled && !this.isStarted) {
            this.isStarted = true;

            this.onStart();
        } else {
            if (this.isKilled)
                throw new KilledAgentException();

            // Agent is already started.
            throw new AlreadyStartedAgentException();
        }
    }

    /**
     * Method call when the agent is started in the method {@link #start()}.
     */
    public abstract void onStart();

    /**
     * Kill the agent. When an agent is killed, it cannot be restarted.
     * <p>
     * When an agent is killed, it stops to play all its behaviors, leaves all the environments where it was evolving
     * and call the method {@link #onKill()}.
     *
     * @throws AlreadyKilledAgentException if the agent have already been killed
     */
    public void kill() throws AlreadyKilledAgentException {
        if (!this.isKilled()) {
            this.isStarted = false;
            this.isKilled = true;

            // Stop playing all behaviors.
            Set<Map.Entry<String, Behavior>> behaviors = this.mapBehaviors.entrySet();
            for (Map.Entry<String, Behavior> behaviorEntry : behaviors) {
                Behavior behavior = behaviorEntry.getValue();
                behavior.stopPlaying();
            }

            // Leave all environments.
            Set<Map.Entry<String, Environment>> environments = this.mapEnvironments.entrySet();
            for (Map.Entry<String, Environment> environmentEntry : environments) {
                this.leaveEnvironment(environmentEntry.getValue());
            }

            this.onKill();
        } else
            throw new AlreadyKilledAgentException();
    }

    /**
     * Method call when the agent is killed in the method {@link #kill()}
     */
    public abstract void onKill();

    /**
     * @param environment the environment that the agent want join
     * @return true if the agent has joined the environment, else false.
     */
    public boolean joinEnvironment(Environment environment) {
        if (this.mapEnvironments.get(environment.getName()) == null) {
            if (environment.acceptAgent(this)) {
                this.mapEnvironments.put(environment.getName(), environment);
                return true;
            } else
                return false;
        } else
            return false;
    }

    /**
     * @param environment the environment
     * @return true if the agent is evolving in the environment, else false.
     */
    public boolean isEvolvingInEnvironment(Environment environment) {
        return environment.isEvolving(this);
    }

    /**
     * @param environmentName the environment name
     * @return true if the agent is evolving in the environment, else false.
     */
    public boolean isEvolvingInEnvironment(String environmentName) {
        Environment environment = this.mapEnvironments.get(environmentName);
        if (environment != null) {
            return environment.isEvolving(this);
        }

        return false;
    }


    /**
     * Makes that the agent leaves the environment.
     *
     * @param environment the environment to leave
     */
    public void leaveEnvironment(Environment environment) {
        environment.leave(this);
        this.mapEnvironments.remove(environment.getName());
    }

    /**
     * Makes that the agent leaves the environment.
     *
     * @param environmentName the environment name
     */
    public void leaveEnvironment(String environmentName) {
        Environment environment = this.mapEnvironments.get(environmentName);
        if (environment != null) {
            environment.leave(this);
            this.mapEnvironments.remove(environmentName);
        }
    }

    /**
     * Search if the behavior is a behavior of the agent, if it is the case, call the method
     * {@link Behavior#startPlaying()}.
     *
     * @param behaviorClass the class of the behavior that we want starting to play
     */
    public void startPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        if (this.isStarted) {
            Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());
            if (behavior != null)
                behavior.startPlaying();
        }
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

    public Map<String, Environment> getMapEnvironments() {
        return Collections.unmodifiableMap(mapEnvironments);
    }

    public Map<String, Behavior> getMapBehaviors() {
        return Collections.unmodifiableMap(this.mapBehaviors);
    }

    public Map<String, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isKilled() {
        return isKilled;
    }
}
