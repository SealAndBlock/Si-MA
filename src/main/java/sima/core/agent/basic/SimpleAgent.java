package sima.core.agent.basic;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;

import java.util.Map;

public class SimpleAgent extends AbstractAgent {

    // Constructors.

    public SimpleAgent(String agentName, int numberId, Map<String, String> args) {
        super(agentName, numberId, args);
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
