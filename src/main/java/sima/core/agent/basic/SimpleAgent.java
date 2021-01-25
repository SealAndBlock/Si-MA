package sima.core.agent.basic;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;

import java.util.Map;

public class SimpleAgent extends AbstractAgent {

    // Constructors.

    public SimpleAgent(String agentName, int sequenceId, int uniqueId, Map<String, String> args) {
        super(agentName, sequenceId, uniqueId, args);
    }

    // Methods.

    @Override
    protected void onnStart() {
        // Nothing.
    }

    @Override
    protected void onKill() {
        // Nothing
    }

    /**
     * @param event the event received
     */
    @Override
    protected void processNoProtocolEvent(Event event) {
        // Nothing.
    }
}
