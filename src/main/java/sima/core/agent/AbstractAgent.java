package sima.core.agent;

import sima.core.agent.exception.AgentNotStartedException;
import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.environment.event.NoProtocolEvent;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractAgent implements EventCatcher {

    // Variables.

    /**
     * The {@link UUID} of the sima.core.agent.
     * <p>
     * This id is use for the simulation to identify several sima.core.agent. It can be also use in simulation to
     * identify sima.core.agent but it can be ignore and the identification of sima.core.agent can be done with
     * protocols defined by the user.
     */
    private final UUID uuid;

    /**
     * The name of the sima.core.agent
     */
    private final String agentName;

    /**
     * A number greater or equal to 0. This number is define by the {@code Simulator}.
     */
    private final int numberId;

    private final AgentIdentifier agentIdentifier;

    /**
     * The several environments where the sima.core.agent evolves.
     * <p>
     * Associate the sima.core.environment name get with the method {@link Environment#getEnvironmentName()} and the
     * instance of the sima.core.environment.
     */
    private final Map<String, Environment> mapEnvironments;

    /**
     * The several behaviors that the sima.core.agent can have.
     * <p>
     * Associate the name of the class of the sima.core.behavior and the instance of the sima.core.behavior.
     */
    private final Map<String, Behavior> mapBehaviors;

    /**
     * The several protocols that the sima.core.agent can use.
     * <p>
     * Associate the {@link ProtocolIdentifier} and the instance of the sima.core.protocol.
     */
    private final Map<ProtocolIdentifier, Protocol> mapProtocol;

    /**
     * True if the sima.core.agent is started, else false.
     */
    private boolean isStarted = false;

    /**
     * True if the sima.core.agent is killed, else false. If an sima.core.agent is killed, it stops to be started and
     * cannot become started again.
     */
    private boolean isKilled = false;

    // Constructors.

    /**
     * Constructs an sima.core.agent with a name and no environments, behaviors and protocols.
     *
     * @param agentName the agent name
     * @param numberId  the number Id of the agent
     * @param args      the map of argument
     * @throws NullPointerException     if the agentName is null.
     * @throws IllegalArgumentException if the numberId is less than 0.
     */
    protected AbstractAgent(String agentName, int numberId, Map<String, String> args) {
        this.uuid = UUID.randomUUID();

        this.numberId = numberId;
        if (this.numberId < 0)
            throw new IllegalArgumentException("The numberId must be greater or equal to 0, the current numberId = " +
                    this.numberId);

        this.agentName = agentName;
        if (this.agentName == null)
            throw new NullPointerException("The sima.core.agent name cannot be null.");

        this.agentIdentifier = new AgentIdentifier(this.uuid, this.agentName, this.numberId);

        this.mapEnvironments = new HashMap<>();
        this.mapBehaviors = new HashMap<>();
        this.mapProtocol = new HashMap<>();

        if (args != null)
            this.processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments map (map argument name with the argument)
     */
    protected abstract void processArgument(Map<String, String> args);

    /**
     * Only use the attributes {@link #uuid} and {@link #agentName} to compare two agents.
     *
     * @param o the object to compare to the sima.core.agent
     * @return true if the object is equal to the sima.core.agent.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAgent)) return false;
        AbstractAgent that = (AbstractAgent) o;
        return uuid.equals(that.uuid) &&
                agentName.equals(that.agentName);
    }

    /**
     * Compute the hash code of the sima.core.agent. Use only the attribute {@link #uuid} and {@link #agentName} to
     * compute the hash code.
     *
     * @return the hash code of the sima.core.agent.
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid, agentName);
    }

    /**
     * Start the sima.core.agent.
     * <p>
     * When an sima.core.agent is starting, the method {@link #onStart()} is called.
     *
     * @throws KilledAgentException         if the sima.core.agent is killed
     * @throws AlreadyStartedAgentException if the sima.core.agent have already been started
     */
    public synchronized final void start() {
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
     * Method call when the sima.core.agent is started in the method {@link #start()}.
     */
    public abstract void onStart();

    /**
     * Kill the sima.core.agent. When an sima.core.agent is killed, it cannot be restarted.
     * <p>
     * When an sima.core.agent is killed, it stops to play all its behaviors, leaves all the environments where it was
     * evolving and call the method {@link #onKill()}.
     *
     * @throws AlreadyKilledAgentException if the sima.core.agent have already been killed
     */
    public synchronized final void kill() {
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
     * Method call when the sima.core.agent is killed in the method {@link #kill()}
     */
    public abstract void onKill();

    /**
     * @param environment the sima.core.environment that the sima.core.agent want join
     * @return true if the sima.core.agent has joined the sima.core.environment, else false.
     */
    public synchronized boolean joinEnvironment(Environment environment) {
        if (this.mapEnvironments.get(environment.getEnvironmentName()) == null) {
            if (environment.acceptAgent(this.getAgentIdentifier())) {
                this.mapEnvironments.put(environment.getEnvironmentName(), environment);
                return true;
            } else
                return false;
        } else
            return false;
    }

    /**
     * @param environment the sima.core.environment
     * @return true if the sima.core.agent is evolving in the sima.core.environment, else false.
     */
    public synchronized boolean isEvolvingInEnvironment(Environment environment) {
        return environment.isEvolving(this.getAgentIdentifier());
    }

    /**
     * @param environmentName the sima.core.environment name
     * @return true if the sima.core.agent is evolving in the sima.core.environment, else false.
     */
    public synchronized boolean isEvolvingInEnvironment(String environmentName) {
        Environment environment = this.mapEnvironments.get(environmentName);
        if (environment != null) {
            return environment.isEvolving(this.getAgentIdentifier());
        }

        return false;
    }


    /**
     * Makes that the sima.core.agent leaves the sima.core.environment.
     *
     * @param environment the sima.core.environment to leave
     */
    public synchronized void leaveEnvironment(Environment environment) {
        environment.leave(this.getAgentIdentifier());
        this.mapEnvironments.remove(environment.getEnvironmentName());
    }

    /**
     * Makes that the sima.core.agent leaves the sima.core.environment.
     *
     * @param environmentName the sima.core.environment name
     */
    public synchronized void leaveEnvironment(String environmentName) {
        Environment environment = this.mapEnvironments.get(environmentName);
        if (environment != null) {
            environment.leave(this.getAgentIdentifier());
            this.mapEnvironments.remove(environmentName);
        }
    }

    /**
     * Add the sima.core.behavior to the sima.core.agent. If the sima.core.agent already have this sima.core.behavior, nothing is done and returns false.
     * <p>
     * In the case where the sima.core.agent has not already the sima.core.behavior, this method creates a new instance of the sima.core.behavior
     * class. If the creation of the instance is a success, the sima.core.behavior is added to the sima.core.agent and returns true, else
     * the sima.core.behavior is not added in the sima.core.agent and returns false.
     *
     * @param behaviorClass the sima.core.behavior class
     * @param args          the argument to transfer to the sima.core.behavior
     * @return true if the sima.core.behavior has been added to the sima.core.agent, else false.
     */
    public synchronized boolean addBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> args) {
        if (this.mapBehaviors.get(behaviorClass.getName()) == null)
            try {
                Constructor<? extends Behavior> constructor = behaviorClass.
                        getConstructor(AbstractAgent.class, Map.class);
                Behavior behavior = constructor.newInstance(this, args);
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
     * Search if the sima.core.behavior is a sima.core.behavior of the sima.core.agent, if it is the case, call the method
     * {@link Behavior#startPlaying()}.
     *
     * @param behaviorClass the class of the sima.core.behavior that we want starting to play
     */
    public synchronized void startPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        if (this.isStarted) {
            Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());
            if (behavior != null)
                behavior.startPlaying();
        }
    }

    /**
     * Search if the sima.core.behavior is a sima.core.behavior of the sima.core.agent, if it is the case, call the method
     * {@link Behavior#stopPlaying()}.
     *
     * @param behaviorClass the class of the sima.core.behavior that we want stopping to play
     */
    public synchronized void stopPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            behavior.stopPlaying();
    }

    /**
     * @param behavior the sima.core.behavior
     * @return true if the sima.core.agent can play the specified sima.core.behavior, else false.
     */
    public boolean canPlayBehavior(Behavior behavior) {
        return behavior.canBePlayedBy(this);
    }

    /**
     * Look if the sima.core.behavior is playing by the sima.core.agent by calling the function {@link Behavior#isPlaying()}
     *
     * @param behaviorClass the class of the sima.core.behavior
     * @return true if the specified sima.core.behavior is playing by the sima.core.agent, else false.
     */
    public synchronized boolean isPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = this.mapBehaviors.get(behaviorClass.getName());

        if (behavior != null)
            return behavior.isPlaying();
        else
            return false;
    }

    /**
     * @param protocolIdentifier the string which identify the sima.core.protocol
     * @return the sima.core.protocol associate to the sima.core.protocol class, if no sima.core.protocol is associated
     * to this class, return null.
     */
    public synchronized Protocol getProtocol(ProtocolIdentifier protocolIdentifier) {
        return this.mapProtocol.get(protocolIdentifier);
    }

    /**
     * Creates a new instance of the specified protocol class. After that, verifies if the agent has already not an
     * instance of protocol with the same {@link ProtocolIdentifier}. If it not the case, the protocol is add to
     * the agent, else the protocol is not add to the agent and returns false.
     *
     * @param protocolClass the class of the protocol
     * @param protocolTag   the tag of the protocol
     * @param args          the arguments map to transfer to the protocol
     * @return true if the instance of the protocol is added to the agent, else false.
     */
    public synchronized boolean addProtocol(Class<? extends Protocol> protocolClass, String protocolTag, Map<String, String> args) {
        try {
            Constructor<? extends Protocol> protocolClassConstructor = protocolClass.getConstructor(String.class,
                    AbstractAgent.class, Map.class);
            Protocol protocol = protocolClassConstructor.newInstance(protocolTag, this, args);
            ProtocolIdentifier protocolIdentifier = protocol.getIdentifier();
            if (!this.mapProtocol.containsKey(protocolIdentifier)) {
                this.mapProtocol.put(protocolIdentifier, protocol);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Method called by an sima.core.environment when an event occurs and that the receiver is the sima.core.agent. This method is here to
     * allow the sima.core.agent to manage how the event must be treated.
     * <p>
     * If the event is a <i>general event</i>, the method {@link #treatNoProtocolEvent(Event)} is called.
     * <p>
     * If the event has a sima.core.protocol targeted, then the sima.core.agent search the associated sima.core.protocol and call the method
     * {@link Protocol#processEvent(Event)} is the sima.core.protocol is find. In the case where the targeted sima.core.protocol is not find
     * among all sima.core.protocol that the sima.core.agent possesses, the method {@link #treatEventWithNotFindProtocol(Event)} is called.
     *
     * @param event the event received
     * @throws AgentNotStartedException if the agent is not started
     * @see NoProtocolEvent
     * @see Event#isNoProtocolEvent()
     */
    @Override
    public synchronized final void processEvent(Event event) {
        if (this.isStarted) {
            if (event.isNoProtocolEvent()) {
                Protocol protocolTarget = this.getProtocol(event.getProtocolTargeted());
                if (protocolTarget != null) {
                    protocolTarget.processEvent(event);
                } else {
                    this.treatEventWithNotFindProtocol(event);
                }
            } else {
                this.treatNoProtocolEvent(event);
            }
        } else {
            throw new AgentNotStartedException("The agent " + this.agentIdentifier + " is not started, cannot " +
                    "process Event.");
        }
    }

    /**
     * This method is called when the sima.core.agent received a <i>general event</i>. This method allows the sima.core.agent to treat all
     * general event that it receives.
     *
     * @param event the event received
     * @see NoProtocolEvent
     * @see Event#isNoProtocolEvent()
     */
    protected abstract void treatNoProtocolEvent(Event event);

    /**
     * This method is called whe the sima.core.agent received an event with a target sima.core.protocol, but the sima.core.agent does not have this
     * sima.core.protocol. This method allows the sima.core.agent to treat this type of event.
     *
     * @param event the event received
     */
    protected abstract void treatEventWithNotFindProtocol(Event event);

    public AgentIdentifier getAgentIdentifier() {
        return this.agentIdentifier;
    }

    /**
     * @return a new instance of {@link AgentInfo} which contains all information about the sima.core.agent.
     */
    public AgentInfo getInfo() {
        return new AgentInfo(this.getAgentIdentifier(), new ArrayList<>(this.mapBehaviors.keySet()),
                new ArrayList<>(this.mapProtocol.keySet()), new ArrayList<>(this.mapEnvironments.keySet()));
    }

    // Getters and Setters.

    public UUID getUUID() {
        return this.uuid;
    }

    public String getAgentName() {
        return this.agentName;
    }

    public int getNumberId() {
        return this.numberId;
    }

    public Map<String, Environment> getMapEnvironments() {
        return Collections.unmodifiableMap(this.mapEnvironments);
    }

    public Map<String, Behavior> getMapBehaviors() {
        return Collections.unmodifiableMap(this.mapBehaviors);
    }

    public Map<ProtocolIdentifier, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isKilled() {
        return isKilled;
    }
}
