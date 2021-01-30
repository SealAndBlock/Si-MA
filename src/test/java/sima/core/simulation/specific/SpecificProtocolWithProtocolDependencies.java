package sima.core.simulation.specific;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.EventSender;

import java.util.Map;

public class SpecificProtocolWithProtocolDependencies extends SpecificProtocolTesting {

    // Variables.

    private SpecificProtocolTesting protocolTesting;
    private EventSender eventSender;

    // Constructors.

    public SpecificProtocolWithProtocolDependencies(String protocolTag, AbstractAgent agentOwner,
                                                    Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    // Getters and Setters.

    public SpecificProtocolTesting getProtocolTesting() {
        return protocolTesting;
    }

    public EventSender getEventSender() {
        return eventSender;
    }
}
