package sima.core.environment.basic;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.simulation.SimaSimulation;

import java.util.Map;

import static sima.core.simulation.SimaSimulationUtils.randomLong;

public class FullyConnectedNetworkEnvironment extends Environment {

    // Static.

    public static final String MIN_SEND_DELAY_ARGS = "minSendDelay";
    public static final String MAX_SEND_DELAY_ARGS = "maxSendDelay";

    public static final long DEFAULT_MIN_SEND_DELAY = 10;
    public static final long DEFAULT_MAX_SEND_DELAY = 20;

    // Variables.

    private long minSendDelay;
    private long maxSendDelay;

    // Constructors.

    public FullyConnectedNetworkEnvironment(String environmentName, Map<String, String> args) {
        super(environmentName, args);
        initSendDelay();
        processArgument(args);
    }

    // Methods.

    protected void processArgument(Map<String, String> args) {
        if (args != null)
            extractSendDelays(args);
    }

    private void initSendDelay() {
        minSendDelay = DEFAULT_MIN_SEND_DELAY;
        maxSendDelay = DEFAULT_MAX_SEND_DELAY;
    }

    private void extractSendDelays(Map<String, String> args) {
        setFromArgsMinSendDelay(args);
        setFromArgsMaxSendDelay(args);
        verifiesDelays();
    }

    /**
     * Verifies if {@link #minSendDelay} is really less or equal to {@link #maxSendDelay}. If it is not the case,
     * inverse values to make it valid.
     */
    private void verifiesDelays() {
        if (maxSendDelay < minSendDelay) {
            long tmp = maxSendDelay;
            maxSendDelay = minSendDelay;
            minSendDelay = tmp;
        }
    }

    private void setFromArgsMinSendDelay(Map<String, String> args) {
        if (args.containsKey(MIN_SEND_DELAY_ARGS))
            try {
                minSendDelay = Integer.parseInt(args.get(MIN_SEND_DELAY_ARGS));
            } catch (NumberFormatException e) {
                minSendDelay = DEFAULT_MIN_SEND_DELAY;
            }

        if (minSendDelay < 1)
            minSendDelay = 1;
    }

    private void setFromArgsMaxSendDelay(Map<String, String> args) {
        if (args.containsKey(MAX_SEND_DELAY_ARGS)) {
            try {
                maxSendDelay = Integer.parseInt(args.get(MAX_SEND_DELAY_ARGS));
            } catch (NumberFormatException e) {
                maxSendDelay = DEFAULT_MAX_SEND_DELAY;
            }
        }

        if (maxSendDelay < 1)
            maxSendDelay = 1;
    }

    /**
     * All agents are accepted.
     *
     * @param abstractAgentIdentifier the {@link AgentIdentifier} of the agent to verify
     * @return always true
     */
    @Override
    protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
        return true;
    }

    @Override
    protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
        // Nothing.
    }

    /**
     * All agents are connected, therefore all event can be sent to any agent.
     *
     * @param receiver the agent receiver
     * @param event    the event to send to the receiver
     * @return always true
     */
    @Override
    protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
        return true;
    }

    @Override
    protected void scheduleEventReception(AgentIdentifier receiver, Event event) {
        SimaSimulation.getScheduler().scheduleEvent(event, randomLong(minSendDelay, maxSendDelay));
    }

    // Getters.

    public long getMinSendDelay() {
        return minSendDelay;
    }

    public long getMaxSendDelay() {
        return maxSendDelay;
    }
}