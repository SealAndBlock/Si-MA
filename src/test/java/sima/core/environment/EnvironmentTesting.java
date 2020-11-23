package sima.core.environment;

import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.simulation.SimaSimulation;

import java.util.Map;

public class EnvironmentTesting extends Environment {

    public static final String ENV_TEST_NAME = "ENV_TEST";

    public static long SEND_DELAY = 10;

    // Constructors.

    public EnvironmentTesting() {
        super(ENV_TEST_NAME, null);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
        // Nothings.
    }

    @Override
    protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
        return true;
    }

    @Override
    protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
        // Nothing
    }

    /**
     * Sends to all agents the event.
     *
     * @param event the event without receiver to send
     */
    @Override
    protected void sendEventWithNullReceiver(Event event) {
        for (AgentIdentifier agentIdentifier : this.getEvolvingAgentIdentifiers()) {
            this.verifyAndScheduleEvent(agentIdentifier, event);
        }
    }

    @Override
    protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
        return true;
    }

    @Override
    protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {
        SimaSimulation.getScheduler().scheduleEvent(event.cloneAndAddReceiver(receiver), SEND_DELAY);
    }

    @Override
    public void processEvent(Event event) {
        // Nothing
    }
}
