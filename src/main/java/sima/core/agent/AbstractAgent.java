package sima.core.agent;

import org.jetbrains.annotations.NotNull;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.exception.AgentNotStartedException;
import sima.core.exception.AlreadyKilledAgentException;
import sima.core.exception.AlreadyStartedAgentException;
import sima.core.exception.KilledAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static sima.core.simulation.SimaSimulation.SIMA_LOG;
import static sima.core.utils.Utils.instantiate;

public abstract class AbstractAgent implements EventCatcher {

    // Variables.

    /**
     * The name of the sima.core.agent
     */
    private final String agentName;

    /**
     * A number greater or equal to 0. The id of the agent in function of the sequence construction where it was
     * create.
     */
    private final int sequenceId;

    /**
     * A number greater or equal to 0. This number must be the only ide define for an agent, For example if one agent as
     * the id 1, only this agent can have the id 1.
     */
    private final int uniqueId;

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
     * @param agentName  the agent name
     * @param sequenceId the sequenceId
     * @param uniqueId   the number Id of the agent
     * @param args       the map of argument
     * @throws IllegalArgumentException if the numberId is less than 0.
     * @throws NullPointerException     if agentName is null.
     */
    protected AbstractAgent(String agentName, int sequenceId, int uniqueId, Map<String, String> args) {
        this.sequenceId = sequenceId;
        if (sequenceId < 0)
            throw new IllegalArgumentException("The sequenceId must be greater or equal to 0, the current sequenceId "
                                                       + "= " + sequenceId);

        this.uniqueId = uniqueId;
        if (uniqueId < 0)
            throw new IllegalArgumentException(
                    "The uniqueId must be greater or equal to 0, the current uniqueId = " + uniqueId);

        this.agentName = Optional.of(agentName).get();
        agentIdentifier = new AgentIdentifier(agentName, sequenceId, uniqueId);
        mapEnvironments = new HashMap<>();
        mapBehaviors = new HashMap<>();
        mapProtocol = new HashMap<>();

        SIMA_LOG.info(this + " CREATED");
    }

    // Methods.

    @Override
    public String toString() {
        return "[AGENT - " +
                "class=" + this.getClass().getName() +
                ", name=" + agentName +
                ", sequenceId=" + sequenceId +
                ", uniqueId=" + uniqueId + "]";
    }

    /**
     * Only use the attributes and {@link #agentName} to compare two agents.
     *
     * @param o the object to compare to the sima.core.agent
     * @return true if the object is equal to the sima.core.agent.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAgent)) return false;
        AbstractAgent that = (AbstractAgent) o;
        return agentName.equals(that.agentName) && sequenceId == that.sequenceId && uniqueId == that.uniqueId;
    }

    /**
     * Compute the hash code of the sima.core.agent. Use the hashCode generate by the {@link #getAgentIdentifier()} of
     * the agent.
     *
     * @return the hash code of the sima.core.agent.
     */
    @Override
    public int hashCode() {
        return getAgentIdentifier().hashCode();
    }

    /**
     * Start the sima.core.agent.
     * <p>
     * When an sima.core.agent is starting, the method {@link #onnStart()} is called.
     *
     * @throws KilledAgentException         if the sima.core.agent is killed
     * @throws AlreadyStartedAgentException if the sima.core.agent have already been started
     */
    public synchronized final void start() {
        if (!isKilled && !isStarted) {
            setStarted();
            onnStart();
        } else {
            if (isKilled)
                throw new KilledAgentException();
            else
                throw new AlreadyStartedAgentException();
        }
    }

    private void setStarted() {
        isStarted = true;
        SIMA_LOG.info(this + " STARTED");
    }

    /**
     * Method call when the sima.core.agent is started in the method {@link #start()}.
     */
    protected abstract void onnStart();

    /**
     * Kill the sima.core.agent. When an sima.core.agent is killed, it cannot be restarted.
     * <p>
     * When an sima.core.agent is killed, it stops to play all its behaviors, leaves all the environments where it was
     * evolving and call the method {@link #onKill()}.
     *
     * @throws AlreadyKilledAgentException if the sima.core.agent have already been killed
     */
    public synchronized final void kill() {
        if (!isKilled()) {
            setKilled();
            stopPlayingAllBehaviors();
            leaveAllEnvironments();
            onKill();
        } else
            throw new AlreadyKilledAgentException();
    }

    private void setKilled() {
        isStarted = false;
        isKilled = true;
        SIMA_LOG.info(this + " KILLED");
    }

    private void stopPlayingAllBehaviors() {
        List<Behavior> behaviors = getBehaviorList();
        for (Behavior behavior : behaviors) {
            behavior.stopPlaying();
        }
    }

    private void leaveAllEnvironments() {
        List<Environment> environments = getEnvironmentList();
        for (Environment environment : environments) {
            leaveEnvironment(environment);
        }
    }

    /**
     * Method call when the sima.core.agent is killed in the method {@link #kill()}
     */
    protected abstract void onKill();

    /**
     * @param environment the sima.core.environment that the sima.core.agent want join
     * @return true if the sima.core.agent has joined the sima.core.environment, else false.
     * @throws NullPointerException if environment is null
     */
    public synchronized boolean joinEnvironment(Environment environment) {
        if (!environment.isEvolving(this.getAgentIdentifier())) {
            if (environment.acceptAgent(getAgentIdentifier())) {
                addEvolvingEnvironment(environment);
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void addEvolvingEnvironment(Environment environment) {
        mapEnvironments.put(environment.getEnvironmentName(), environment);
    }


    /**
     * Set the environment in {@link #mapEnvironments} if the agent is evolving in the specified environment and that
     * the agent does not know that it is evolving in it. In other word, if the agent is evolving in the specified
     * environment and that the environment is not in {@link #mapEnvironments}, therefore add the environment in {@link
     * #mapEnvironments}
     *
     * @param environment the environment where the agent is evolving
     */
    public synchronized void setEvolvingInEnvironment(Environment environment) {
        if (isUnknownEnvironment(environment)) {
            if (environment.isEvolving(getAgentIdentifier())) {
                addEvolvingEnvironment(environment);
            }
        }
    }

    /**
     * Verifies if the specified environment is unknown or not. An environment is unknown if it is not present in {@link
     * #mapEnvironments}
     *
     * @param environment the environment to verify
     * @return true if the environment specified is unknown, else false.
     */
    private boolean isUnknownEnvironment(Environment environment) {
        return !mapEnvironments.containsKey(environment.getEnvironmentName());
    }

    /**
     * Makes that the sima.core.agent leaves the sima.core.environment.
     *
     * @param environment the sima.core.environment to leave
     * @throws NullPointerException if environment is null
     */
    public synchronized void leaveEnvironment(Environment environment) {
        // Verify with map to avoid loop if Environment recall the method leaveEnvironment
        if (isEvolvingInEnvironment(Optional.of(environment).get())) {
            environment.leave(getAgentIdentifier());
            forgetEnvironment(environment);
        }
    }

    /**
     * Forgets the environment. In others word, remove the environment from {@link #mapEnvironments}.
     *
     * @param environment the environment to forget
     */
    private void forgetEnvironment(Environment environment) {
        mapEnvironments.remove(environment.getEnvironmentName());
    }

    /**
     * Unset the environment in {@link #mapEnvironments} if the agent is not evolving in the environment and that the
     * environment is always in {@link #mapEnvironments}. If the agent is always evolving in the environment, nothing is
     * done.
     *
     * <strong>WARNINGS!</strong> If the agent is evolving <strong>BUT</strong> the agent does not know it (the
     * environment is not in {@link #mapEnvironments}) therefore nothing is done.
     *
     * @param environment the environment where the agent is not evolving anymore
     */
    public synchronized void unSetEvolvingEnvironment(Environment environment) {
        if (mapEnvironments.containsKey(environment.getEnvironmentName())) {
            if (!environment.isEvolving(getAgentIdentifier())) {
                forgetEnvironment(environment);
            }
        }
    }

    /**
     * @param environment the sima.core.environment
     * @return true if the sima.core.agent is evolving in the sima.core.environment, else false.
     */
    public synchronized boolean isEvolvingInEnvironment(Environment environment) {
        if (environment != null)
            return environment.isEvolving(getAgentIdentifier());
        else
            return false;
    }

    /**
     * Add the sima.core.behavior to the sima.core.agent. If the sima.core.agent already have this sima.core.behavior,
     * nothing is done and returns false.
     * <p>
     * In the case where the sima.core.agent has not already the sima.core.behavior, this method creates a new instance
     * of the sima.core.behavior class. If the creation of the instance is a success, the sima.core.behavior is added to
     * the sima.core.agent and returns true, else the sima.core.behavior is not added in the sima.core.agent and returns
     * false.
     *
     * @param behaviorClass the sima.core.behavior class
     * @param behaviorArgs  the argument to transfer to the sima.core.behavior
     * @return true if the sima.core.behavior has been added to the sima.core.agent, else false.
     */
    public synchronized boolean addBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs) {
        if (mapBehaviors.get(behaviorClass.getName()) == null)
            try {
                createAndAddBehavior(behaviorClass, behaviorArgs);
                return true;
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                return false;
            }
        else
            return false;
    }

    /**
     * Construct an instance of the specified Behavior classed.
     *
     * @param behaviorClass the behavior class
     * @param behaviorArgs  the args to pass to the constructor of the {@code Behavior}
     * @return a new instance of the Behavior class specified.
     * @throws NoSuchMethodException     If the Behavior class does not have the correct constructor
     * @throws InstantiationException    If there is a problem during the instantiation
     * @throws IllegalAccessException    If the Behavior constructor is not public
     * @throws InvocationTargetException If the Behavior constructor call throws an exception
     */
    @NotNull
    private Behavior constructBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return instantiate(behaviorClass, new Class[]{AbstractAgent.class, Map.class}, this, behaviorArgs);
    }

    /**
     * Create a new instance of the behavior class specified with the method {@link #constructBehavior(Class, Map)} and
     * add the new instance in {@link #mapBehaviors}.
     *
     * @param behaviorClass the sima.core.behavior class
     * @param behaviorArgs  the argument to transfer to the sima.core.behavior
     * @throws NoSuchMethodException     If the Behavior class does not have the correct constructor
     * @throws InstantiationException    If there is a problem during the instantiation
     * @throws IllegalAccessException    If the Behavior constructor is not public
     * @throws InvocationTargetException If the Behavior constructor call throws an exception
     */
    private void createAndAddBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Behavior behavior = constructBehavior(behaviorClass, behaviorArgs);
        mapBehaviors.put(behaviorClass.getName(), behavior);
        SIMA_LOG.info(this + " ADD BEHAVIOR " + behavior);
    }

    /**
     * Search if the sima.core.behavior is a sima.core.behavior of the sima.core.agent, if it is the case, call the
     * method {@link Behavior#startPlaying()}.
     *
     * @param behaviorClass the class of the sima.core.behavior that we want starting to play
     * @throws NullPointerException if behaviorClass is null
     */
    public synchronized void startPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        if (behaviorClass == null)
            throw new NullPointerException("BehaviorClass null");
        if (isStarted) {
            Behavior behavior = mapBehaviors.get(behaviorClass.getName());
            if (behavior != null)
                behavior.startPlaying();
        }
    }

    /**
     * Search if the sima.core.behavior is a sima.core.behavior of the sima.core.agent, if it is the case, call the
     * method {@link Behavior#stopPlaying()}.
     *
     * @param behaviorClass the class of the sima.core.behavior that we want stopping to play
     * @throws NullPointerException if behaviorClass is null
     */
    public synchronized void stopPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            behavior.stopPlaying();
    }

    /**
     * @param behaviorClass the behavior class
     * @return true if the sima.core.agent can play the specified sima.core.behavior, else false.
     */
    public boolean canPlayBehavior(Class<? extends Behavior> behaviorClass) {
        try {
            constructBehavior(behaviorClass, null);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Look if the sima.core.behavior is playing by the sima.core.agent by calling the function {@link
     * Behavior#isPlaying()}
     *
     * @param behaviorClass the class of the sima.core.behavior
     * @return true if the specified sima.core.behavior is playing by the sima.core.agent, else false.
     */
    public synchronized boolean isPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        Behavior behavior = mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            return behavior.isPlaying();
        else
            return false;
    }

    /**
     * @param behaviorClass the class of the behavior
     * @return the instance of the corresponding behavior if the agent has added the behavior before, else null.
     */
    public synchronized Behavior getBehavior(Class<? extends Behavior> behaviorClass) {
        return mapBehaviors.get(behaviorClass.getName());
    }

    /**
     * Creates a new instance of the specified protocol class. After that, verifies if the agent has already not an
     * instance of protocol with the same {@link ProtocolIdentifier}. If it not the case, the protocol is add to the
     * agent, else the protocol is not add to the agent and returns false.
     *
     * @param protocolClass the class of the protocol
     * @param protocolTag   the tag of the protocol
     * @param args          the arguments map to transfer to the protocol
     * @return true if the instance of the protocol is added to the agent, else false.
     */
    public synchronized boolean addProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                            Map<String, String> args) {
        try {
            Protocol protocol = constructProtocol(protocolClass, protocolTag, args);
            ProtocolIdentifier protocolIdentifier = protocol.getIdentifier();
            if (isNotAddedProtocol(protocolIdentifier)) {
                mapProtocol(protocolIdentifier, protocol);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            return false;
        }
    }

    private void mapProtocol(ProtocolIdentifier protocolIdentifier, Protocol protocol) {
        mapProtocol.put(protocolIdentifier, protocol);
        SIMA_LOG.info(this + " ADD PROTOCOL " + protocol);
    }

    private boolean isNotAddedProtocol(ProtocolIdentifier protocolIdentifier) {
        return !mapProtocol.containsKey(protocolIdentifier);
    }

    /**
     * Construct an instance of the specified Protocol classed.
     *
     * @param protocolClass the protocol class
     * @param protocolTag   the tag of the protocol instance
     * @param protocolArgs  the protocol arguments
     * @return a new instance of the Protocol class specified.
     * @throws NoSuchMethodException     If the Protocol class does not have the correct constructor
     * @throws InstantiationException    If there is a problem during the instantiation
     * @throws IllegalAccessException    If the Protocol constructor is not public
     * @throws InvocationTargetException If the Protocol constructor call throws an exception
     */
    @NotNull
    private Protocol constructProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                       Map<String, String> protocolArgs)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return instantiate(protocolClass, new Class[]{String.class, AbstractAgent.class, Map.class}, protocolTag,
                           this, protocolArgs);
    }

    /**
     * @param protocolIdentifier the string which identify the sima.core.protocol
     * @return the sima.core.protocol associate to the sima.core.protocol class, if no sima.core.protocol is associated
     * to this class, return null.
     */
    public synchronized Protocol getProtocol(ProtocolIdentifier protocolIdentifier) {
        return mapProtocol.get(protocolIdentifier);
    }

    /**
     * Method called by an sima.core.environment when an event occurs and that the receiver is the sima.core.agent. This
     * method is here to allow the sima.core.agent to manage how the event must be treated.
     * <p>
     * This method is final and synchronized to hide the synchronisation for the user. Therefore, to manage the
     * treatment of the event, you must override the method {@link #inProcessEvent(Event)}. However, the method {@link
     * #inProcessEvent(Event)} is called only if the agent is started, else this methods throws {@link
     * AgentNotStartedException}.
     *
     * @param event the event received
     * @throws AgentNotStartedException if the agent is not started
     * @see #inProcessEvent(Event)
     */
    @Override
    public final synchronized void processEvent(Event event) {
        if (isStarted)
            inProcessEvent(event);
        else
            throw new AgentNotStartedException("The agent " + agentIdentifier + " is not started, cannot " +
                                                       "process Event.");
    }

    /**
     * This method is called in the method {@link #processEvent(Event)}. In that way this method is not synchronized and
     * the user must not have to be preoccupy by synchronisation and multi threading. This method is called by
     * processEvent only if the agent is started.
     * <p>
     * The default implementation is in first the verification of if the event have a protocol targeted or not. If it
     * is not the case, throws {@link UnsupportedOperationException}. Else if the event has a protocol targeted, the
     * method search if the agent add the protocol and if it is the case, call the method
     * {@link Protocol#processEvent(Event)} of the protocol. If the protocol targeted is not add in the agent, throws
     * {@link IllegalArgumentException}.
     *
     * @param event the event to process
     *
     * @throws IllegalArgumentException if the event has a protocol targeted which is not added in the agent
     * @throws UnsupportedOperationException if the event has no protocol targeted (equals to null)
     */
    protected void inProcessEvent(Event event) {
        if (event.isProtocolEvent()) {
            Protocol protocolTarget = getProtocol(event.getProtocolTargeted());
            if (protocolTarget != null)
                protocolTarget.processEvent(event);
            else
                throw new IllegalArgumentException("Event with not added protocol");
        } else
            throw new UnsupportedOperationException("No operation for event with no protocol targeted");
    }

    /**
     * This method is called when the sima.core.agent received a <i>general event</i>. This method allows the
     * sima.core.agent to treat all general event that it receives.
     *
     * @param event the event received
     * @see Event#isProtocolEvent()
     */
    protected abstract void processNoProtocolEvent(Event event);

    public AgentIdentifier getAgentIdentifier() {
        return agentIdentifier;
    }

    /**
     * @return a new instance of {@link AgentInfo} which contains all information about the sima.core.agent.
     */
    public AgentInfo getInfo() {
        return new AgentInfo(getAgentIdentifier(), new ArrayList<>(mapBehaviors.keySet()),
                             new ArrayList<>(mapProtocol.keySet()), new ArrayList<>(mapEnvironments.keySet()));
    }

    // Getters and Setters.

    public String getAgentName() {
        return agentName;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public List<Environment> getEnvironmentList() {
        return new ArrayList<>(mapEnvironments.values());
    }

    public List<Behavior> getBehaviorList() {
        return new ArrayList<>(mapBehaviors.values());
    }

    public List<Protocol> getProtocolList() {
        return new ArrayList<>(mapProtocol.values());
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isKilled() {
        return isKilled;
    }
}
