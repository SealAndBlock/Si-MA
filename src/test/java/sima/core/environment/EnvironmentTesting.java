package sima.core.environment;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;

import java.util.*;

public class EnvironmentTesting extends Environment {

    // Static.

    public static final String ENV_TEST_NAME = "ENV_TEST";

    // Variables.

    /**
     * Only to allow several environment with same name.
     * <p>
     * With the uui id, two environments with the same name has different hashcode.
     */
    private final UUID uuid;

    private final List<AgentIdentifier> notAcceptedAgentList = new Vector<>();

    // Constructors.

    /**
     * Required constructor for Environment.
     *
     * @param name the environment name
     * @param args the environment arguments
     */
    public EnvironmentTesting(String name, Map<String, String> args) {
        super(name, args);
        uuid = UUID.randomUUID();
    }

    public EnvironmentTesting(int number) {
        this(ENV_TEST_NAME + "_" + number, null);
    }

    public EnvironmentTesting(int number, List<AgentIdentifier> notAcceptedAgentList) {
        this(number);

        if (notAcceptedAgentList != null)
            this.notAcceptedAgentList.addAll(notAcceptedAgentList);
    }

    // Methods.

    @Override
    protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
        return !this.notAcceptedAgentList.contains(abstractAgentIdentifier);
    }

    @Override
    protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
        // Nothing
    }

    @Override
    public boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
        return true;
    }

    @Override
    protected void scheduleEventReception(AgentIdentifier receiver, Event event) {
        /*SimaSimulation.getScheduler().scheduleEvent(event.cloneAndAddReceiver(receiver), SEND_DELAY);*/
        // Nothing
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uuid);
    }
}
