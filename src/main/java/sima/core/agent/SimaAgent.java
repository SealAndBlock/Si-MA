package sima.core.agent;

import org.jetbrains.annotations.NotNull;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventProcessor;
import sima.core.exception.*;
import sima.core.protocol.IntendedToProtocol;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.simulation.SimaSimulation;

import java.util.*;

import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.core.utils.Utils.instantiate;

public class SimaAgent implements EventProcessor {

    // Variables.

    /**
     * The name of the {@link SimaAgent}
     */
    private final String agentName;

    /**
     * A number greater or equal to 0. The id of the agent in function of the sequence construction where it was created.
     */
    private final int sequenceId;

    /**
     * A number greater or equal to 0. This number must be the only ide define for an agent, For example if one agent as the id 1, only this agent can
     * have the id 1.
     */
    private final int uniqueId;

    /**
     * The unique identifier of the Agent.
     */
    private final AgentIdentifier agentIdentifier;

    /**
     * The several behaviors that the {@link SimaAgent} can have.
     * <p>
     * Associate the name of the class of the {@link Behavior} and the instance of the {@link Behavior}.
     */
    private final Map<String, Behavior> mapBehaviors;

    /**
     * The several protocols that the {@link SimaAgent} can use.
     * <p>
     * Associate the {@link ProtocolIdentifier} and the instance of the {@link Protocol}.
     */
    private final Map<ProtocolIdentifier, Protocol> mapProtocol;

    /**
     * True if the {@link SimaAgent} is started, else false.
     */
    private boolean isStarted = false;

    /**
     * True if the {@link SimaAgent} is killed, else false. If a {@link SimaAgent} is killed, it stops to be started and cannot become started again.
     */
    private boolean isKilled = false;

    // Constructors.

    /**
     * Constructs a {@link SimaAgent} with a name and no environments, behaviors and protocols.
     *
     * @param agentName  the agent name
     * @param sequenceId the sequenceId
     * @param uniqueId   the number ID of the agent
     * @param args       the map of argument
     *
     * @throws IllegalArgumentException if the numberId is less than 0.
     * @throws NullPointerException     if agentName is null.
     */
    public SimaAgent(String agentName, int sequenceId, int uniqueId, Map<String, String> args) {
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
        mapBehaviors = new HashMap<>();
        mapProtocol = new HashMap<>();

        SimaLog.info(String.format("CREATED %s", this));
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
     * @param o the object to compare to the {@link SimaAgent}
     *
     * @return true if the object is equal to the {@link SimaAgent}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimaAgent that)) return false;
        return agentName.equals(that.agentName) && sequenceId == that.sequenceId && uniqueId == that.uniqueId;
    }

    /**
     * Compute the hash code of the {@link SimaAgent}. Use the hashCode generate by the {@link #getAgentIdentifier()} of the agent.
     *
     * @return the hash code of the {@link SimaAgent}.
     */
    @Override
    public int hashCode() {
        return getAgentIdentifier().hashCode();
    }

    /**
     * Start the {@link SimaAgent}.
     * <p>
     * When a {@link SimaAgent} is starting, the method {@link #onStart()} is called.
     *
     * @throws KilledAgentException         if the {@link SimaAgent} is killed
     * @throws AlreadyStartedAgentException if the {@link SimaAgent} have already been started
     */
    public final synchronized void start() {
        if (!isKilled && !isStarted) {
            setStarted();
            onStart();
        } else {
            if (isKilled)
                throw new KilledAgentException();
            else
                throw new AlreadyStartedAgentException();
        }
    }

    private void setStarted() {
        isStarted = true;

        SimaLog.info(String.format("STARTED %s", this));
    }

    /**
     * Method call when the {@link SimaAgent} is started in the method {@link #start()}.
     */
    protected void onStart() {
        // Nothing is done. Here to allow subclasses to make something during the start.
    }

    /**
     * Kill the {@link SimaAgent}. When a {@link SimaAgent} is killed, it cannot be restarted.
     * <p>
     * When a {@link SimaAgent} is killed, it stops to play all its behaviors, leaves all the environments where it was evolving and call the method
     * {@link #onKill()}.
     *
     * @throws AlreadyKilledAgentException if the {@link SimaAgent} have already been killed
     */
    public final synchronized void kill() {
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

        SimaLog.info(String.format("KILLED %s", this));
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
     * Method call when the {@link SimaAgent} is killed in the method {@link #kill()}
     */
    protected void onKill() {
        // Nothing is done. Here to allow subclasses to make something during the kill.
    }

    /**
     * @param environment the {@link Environment}
     *
     * @return true if the {@link SimaAgent} is evolving in the {@link Environment}, else false.
     */
    public synchronized boolean isEvolvingInEnvironment(Environment environment) {
        if (environment != null)
            return environment.isEvolving(getAgentIdentifier());
        else
            return false;
    }

    /**
     * @param environment the {@link Environment} that the {@link SimaAgent} want join
     *
     * @return true if the {@link SimaAgent} has joined the {@link Environment}, else false.
     *
     * @throws NullPointerException if environment is null
     */
    public synchronized boolean joinEnvironment(@NotNull Environment environment) {
        return environment.acceptAgent(getAgentIdentifier());
    }


    /**
     * Makes that the {@link SimaAgent} leaves the {@link Environment}.
     *
     * @param environment the {@link Environment} to leave
     *
     * @throws NullPointerException if environment is null
     */
    public synchronized void leaveEnvironment(@NotNull Environment environment) {
        environment.leave(getAgentIdentifier());
    }

    /**
     * Add the {@link Behavior} to the {@link SimaAgent}. If the {@link SimaAgent} already have this {@link Behavior}, nothing is done and returns
     * false.
     * <p>
     * In the case where the {@link SimaAgent} has not already the {@link Behavior}, this method creates a new instance of the {@link Behavior}
     * class. If the creation of the instance is a success, the {@link Behavior} is added to the {@link SimaAgent} and returns true, else the
     * {@link Behavior} is not added in the {@link SimaAgent} and returns false.
     *
     * @param behaviorClass the {@link Behavior} class
     * @param behaviorArgs  the argument to transfer to the {@link Behavior}
     *
     * @return true if the {@link Behavior} has been added to the {@link SimaAgent}, else false.
     */
    public synchronized boolean addBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs) {
        if (mapBehaviors.get(behaviorClass.getName()) == null)
            try {
                createAndAddBehavior(behaviorClass, behaviorArgs);
                return true;
            } catch (FailInstantiationException e) {
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
     *
     * @return a new instance of the Behavior class specified.
     *
     * @throws FailInstantiationException if the instantiation fails
     */
    @NotNull
    private Behavior constructBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs)
            throws FailInstantiationException {
        return instantiate(behaviorClass, new Class[]{SimaAgent.class, Map.class}, this, behaviorArgs);
    }

    /**
     * Create a new instance of the behavior class specified with the method {@link #constructBehavior(Class, Map)} and add the new instance in {@link
     * #mapBehaviors}.
     *
     * @param behaviorClass the {@link Behavior} class
     * @param behaviorArgs  the argument to transfer to the {@link Behavior}
     *
     * @throws FailInstantiationException if the instantiation fails
     */
    private void createAndAddBehavior(Class<? extends Behavior> behaviorClass, Map<String, String> behaviorArgs)
            throws FailInstantiationException {
        var behavior = constructBehavior(behaviorClass, behaviorArgs);
        mapBehaviors.put(behaviorClass.getName(), behavior);

        SimaLog.info(String.format("AGENT %s ADD BEHAVIOR %s", this, behavior));
    }

    /**
     * Search if the {@link Behavior} is a {@link Behavior} of the {@link SimaAgent}, if it is the case, call the method {@link
     * Behavior#startPlaying()}.
     *
     * @param behaviorClass the class of the {@link Behavior} that we want starting to play
     *
     * @throws NullPointerException if behaviorClass is null
     */
    public synchronized void startPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        if (behaviorClass == null)
            throw new NullPointerException("BehaviorClass null");
        if (isStarted) {
            var behavior = mapBehaviors.get(behaviorClass.getName());
            if (behavior != null)
                behavior.startPlaying();
        }
    }

    /**
     * Search if the {@link Behavior} is a {@link Behavior} of the {@link SimaAgent}, if it is the case, call the method {@link
     * Behavior#stopPlaying()}.
     *
     * @param behaviorClass the class of the {@link Behavior} that we want stopping to play
     *
     * @throws NullPointerException if behaviorClass is null
     */
    public synchronized void stopPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        var behavior = mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            behavior.stopPlaying();
    }

    /**
     * @param behaviorClass the behavior class
     *
     * @return true if the {@link SimaAgent} can play the specified {@link Behavior}, else false.
     */
    public boolean canPlayBehavior(Class<? extends Behavior> behaviorClass) {
        try {
            // Verify if it is possible to construct the behavior, if it is the case, returns true
            constructBehavior(behaviorClass, null);
            return true;
        } catch (FailInstantiationException e) {
            return false;
        }
    }

    /**
     * Look if the {@link Behavior} is playing by the {@link SimaAgent} by calling the function {@link Behavior#isPlaying()}
     *
     * @param behaviorClass the class of the {@link Behavior}
     *
     * @return true if the specified {@link Behavior} is playing by the {@link SimaAgent}, else false.
     */
    public synchronized boolean isPlayingBehavior(Class<? extends Behavior> behaviorClass) {
        var behavior = mapBehaviors.get(behaviorClass.getName());
        if (behavior != null)
            return behavior.isPlaying();
        else
            return false;
    }

    /**
     * @param behaviorClass the class of the behavior
     *
     * @return the instance of the corresponding behavior if the agent has added the behavior before, else null.
     */
    public synchronized Behavior getBehavior(Class<? extends Behavior> behaviorClass) {
        return mapBehaviors.get(behaviorClass.getName());
    }

    /**
     * Creates a new instance of the specified protocol class. After that, verifies if the agent has already not an instance of protocol with the same
     * {@link ProtocolIdentifier}. If it not the case, the protocol is added to the agent, else the protocol is not add to the agent and returns
     * false.
     *
     * @param protocolClass the class of the protocol
     * @param protocolTag   the tag of the protocol
     * @param args          the arguments map to transfer to the protocol
     *
     * @return true if the instance of the protocol is added to the agent, else false.
     */
    public synchronized boolean addProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                            Map<String, String> args) {
        try {
            var protocol = constructProtocol(protocolClass, protocolTag, args);
            var protocolIdentifier = protocol.getIdentifier();
            if (isNotAddedProtocol(protocolIdentifier)) {
                mapProtocol(protocolIdentifier, protocol);
                return true;
            } else
                return false;
        } catch (FailInstantiationException e) {
            return false;
        }
    }

    private void mapProtocol(ProtocolIdentifier protocolIdentifier, Protocol protocol) {
        mapProtocol.put(protocolIdentifier, protocol);

        SimaLog.info(String.format("AGENT %s ADD PROTOCOL %s", this, protocol));
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
     *
     * @return a new instance of the Protocol class specified.
     *
     * @throws FailInstantiationException if the instantiation fails
     */
    @NotNull
    private Protocol constructProtocol(Class<? extends Protocol> protocolClass, String protocolTag,
                                       Map<String, String> protocolArgs)
            throws FailInstantiationException {
        return instantiate(protocolClass, new Class[]{String.class, SimaAgent.class, Map.class}, protocolTag,
                           this, protocolArgs);
    }

    /**
     * @param protocolIdentifier the string which identify the {@link Protocol}
     *
     * @return the {@link Protocol} associate to the {@link Protocol} class, if no {@link Protocol} is associated to this class, return null.
     */
    public synchronized Protocol getProtocol(ProtocolIdentifier protocolIdentifier) {
        return mapProtocol.get(protocolIdentifier);
    }

    /**
     * Method called by an {@link Environment} when an event occurs and that the receiver is the {@link SimaAgent}. This method is here to allow the
     * {@link SimaAgent} to manage how the event must be treated.
     * <p>
     * This method is final and synchronized to hide the synchronisation for the user. Therefore, to manage the treatment of the event, you must
     * override the method {@link #inProcessEvent(Event)}. However, the method {@link #inProcessEvent(Event)} is called only if the agent is started,
     * else these methods throws {@link AgentNotStartedException}.
     *
     * @param event the event received
     *
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
     * This method is called in the method {@link #processEvent(Event)}. In that way this method is not synchronized and the user must not have to be
     * preoccupied by synchronisation and multi threading. This method is called by {@link #processEvent(Event)} only if the agent is started.
     * <p>
     * This method take all {@link Protocol} added in the agent and call the method {@link Protocol#processEvent(Event)}. Each protocol which think
     * that the {@link Event} must be treated by him treats the {@link Event}.
     *
     * @param event the event to process
     *
     * @throws IllegalArgumentException if the event has a protocol targeted which is not added in the agent
     */
    protected void inProcessEvent(Event event) {
        if (event instanceof IntendedToProtocol intendedToProtocol) {
            var intendedProtocol = getProtocol(intendedToProtocol.getIntendedProtocol());
            if (intendedProtocol != null) {
                intendedProtocol.processEvent(event);
            } else {
                throw new IllegalArgumentException(intendedToProtocol.getIntendedProtocol() + " is not added in the agent");
            }
        } else
            throw new UnsupportedOperationException("The agent " + this + " cannot process event like " + event.getClass());
    }

    public AgentIdentifier getAgentIdentifier() {
        return agentIdentifier;
    }

    /**
     * @return a new instance of {@link AgentInfo} which contains all information about the {@link SimaAgent}.
     */
    public AgentInfo getInfo() {
        List<Environment> environments = SimaSimulation.getAgentEnvironment(getAgentIdentifier());
        return new AgentInfo(getAgentIdentifier(), new ArrayList<>(mapBehaviors.keySet()),
                             new ArrayList<>(mapProtocol.keySet()), getEnvironmentNameList(environments));
    }

    @NotNull
    private List<String> getEnvironmentNameList(List<Environment> environments) {
        List<String> environmentNameList = new ArrayList<>();
        for (Environment environment : environments) {
            environmentNameList.add(environment.getEnvironmentName());
        }
        return environmentNameList;
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
        return SimaSimulation.getAgentEnvironment(agentIdentifier);
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
