package sima.core.agent;

import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.event.GeneralEvent;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentificator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractAgent {

    // Variables.

    /**
     * The {@link UUID} of the agent.
     */
    private final UUID uuid;

    /**
     * The name of the agent
     */
    private final String agentName;

    /**
     * The several environments where the agent evolves.
     * <p>
     * Associate the environment name get with the method {@link Environment#getName()} and the instance of the
     * environment.
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
     * Associate the {@link ProtocolIdentificator} and the instance of the protocol.
     */
    private final Map<ProtocolIdentificator, Protocol> mapProtocol;

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
     * Constructs an agent with a name and no environments, behaviors and protocols.
     *
     * @param agentName the agent name
     */
    public AbstractAgent(String agentName) {
        this.uuid = UUID.randomUUID();

        this.agentName = agentName;

        this.mapEnvironments = new HashMap<>();
        this.mapBehaviors = new HashMap<>();
        this.mapProtocol = new HashMap<>();
    }

    // Methods.

    /**
     * Start the agent.
     * <p>
     * When an agent is starting, the method {@link #onStart()} is called.
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
     * Add the behavior to the agent. If the agent already have this behavior, nothing is done and returns false.
     * <p>
     * In the case where the agent has not already the behavior, this method creates a new instance of the behavior
     * class. If the creation of the instance is a success, the behavior is added to the agent and returns true, else
     * the behavior is not added in the agent and returns false.
     *
     * @param behaviorClass the behavior class
     * @return true if the behavior has been added to the agent, else false.
     */
    public boolean addBehavior(Class<? extends Behavior> behaviorClass) {
        if (this.mapBehaviors.get(behaviorClass.getName()) == null)
            try {
                Constructor<? extends Behavior> constructor = behaviorClass.getConstructor(AbstractAgent.class);
                Behavior behavior = constructor.newInstance(this);
                this.mapBehaviors.put(behaviorClass.getName(), behavior);
                return true;
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                return false;
            }
        else
            return false;
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
     * @param behavior the behavior
     * @return true if the agent can play the specified behavior, else false.
     */
    public boolean canPlayBehavior(Behavior behavior) {
        return behavior.canBePlayedBy(this);
    }

    /**
     * Look if the behavior is playing by the agent by calling the function {@link Behavior#isPlaying()}
     *
     * @param behaviorClass the class of the behavior
     * @return true if the specified behavior is playing by the agent, else false.
     */
    public boolean isPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());

        if (behavior != null)
            return behavior.isPlaying();
        else
            return false;
    }

    /**
     * @param protocolIdentificator the string which identify the protocol
     * @return the protocol associate to the protocol class, if no protocol is associated to this class, return null.
     */
    public Protocol getProtocol(ProtocolIdentificator protocolIdentificator) {
        return this.mapProtocol.get(protocolIdentificator);
    }

    /**
     * Add the protocol to the agent. If the agent has already an instance of protocol which has the class than the
     * specified protocol, the protocol is not added. To update a protocol which has already been added, use
     * {@link #updateProtocol(Protocol)}. If the specified protocol is null, nothing is done and returns false.
     *
     * @param protocol the protocol (must be not null)
     * @return true if the protocol is added, else false.
     * @see #updateProtocol(Protocol)
     */
    public boolean addProtocol(Protocol protocol) {
        if (protocol != null) {
            Protocol older = this.getProtocol(protocol.getIdentificator());
            if (older == null) {
                this.mapProtocol.put(protocol.getIdentificator(), protocol);
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    /**
     * Update the protocol of the agent. It is the same behavior that the method {@link #addProtocol(Protocol)},
     * however the protocol is update even if there is already an instance of protocol with the same class if the
     * specified class in the agent.
     *
     * @param protocol the protocol (must be not null)
     * @return true if the protocol is update, else false.
     */
    public boolean updateProtocol(Protocol protocol) {
        if (protocol != null) {
            Class<? extends Protocol> protocolClass = protocol.getClass();
            String className = protocolClass.getName();

            this.mapProtocol.put(protocol.getIdentificator(), protocol);
            return true;
        } else
            return false;
    }

    /**
     * Method called by an environment when an event occurs and that the receiver is the agent. This method is here to
     * allow the agent to manage how the event must be treated.
     * <p>
     * If the event is a <i>general event</i>, the method {@link #treatGeneralEvent(Event)} is called.
     * <p>
     * If the event has a protocol targeted, then the agent search the associated protocol and call the method
     * {@link Protocol#processEvent(Event)} is the protocol is find. In the case where the targeted protocol is not find
     * among all protocol that the agent possesses, the method {@link #treatEventWithNotFindProtocol(Event)} is called.
     *
     * @param event the event received
     * @see GeneralEvent
     * @see Event#isGeneralEvent()
     */
    public void receivedEvent(Event event) {
        if (event.isGeneralEvent()) {
            Protocol protocolTarget = this.getProtocol(event.getProtocolTargeted());
            if (protocolTarget != null) {
                protocolTarget.processEvent(event);
            } else {
                this.treatEventWithNotFindProtocol(event);
            }
        } else {
            this.treatGeneralEvent(event);
        }
    }

    /**
     * This method is called when the agent received a <i>general event</i>. This method allows the agent to treat all
     * general event that it receives.
     *
     * @param event the event received
     * @see GeneralEvent
     * @see Event#isGeneralEvent()
     */
    protected abstract void treatGeneralEvent(Event event);

    /**
     * This method is called whe the agent received an event with a target protocol, but the agent does not have this
     * protocol. This method allows the agent to treat this type of event.
     *
     * @param event the event received
     */
    protected abstract void treatEventWithNotFindProtocol(Event event);

    /**
     * @return a new instance of {@link AgentInfo} which contains all information about the agent.
     */
    public AgentInfo getInfo() {
        return new AgentInfo(this.uuid, this.agentName);
    }

    // Getters and Setters.

    public UUID getUUID() {
        return this.uuid;
    }

    public String getAgentName() {
        return agentName;
    }

    public Map<String, Environment> getMapEnvironments() {
        return Collections.unmodifiableMap(this.mapEnvironments);
    }

    public Map<String, Behavior> getMapBehaviors() {
        return Collections.unmodifiableMap(this.mapBehaviors);
    }

    public Map<ProtocolIdentificator, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isKilled() {
        return isKilled;
    }
}
