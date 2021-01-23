package sima.core.protocol;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;
import sima.core.protocol.ProtocolManipulator.DefaultProtocolManipulator;

import java.util.Map;

public class ProtocolTesting extends Protocol {

    // Variables.

    private int passToProcessEvent;

    private DefaultProtocolManipulator defaultProtocolManipulator;

    // Constructors.

    public ProtocolTesting(String protocolTag, AbstractAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
        passToProcessEvent = 0;
    }

    // Methods.

    @Override
    public void processEvent(Event event) {
        this.passToProcessEvent++;
    }

    @Override
    protected ProtocolManipulator getDefaultProtocolManipulator() {
        if (this.defaultProtocolManipulator == null)
            this.defaultProtocolManipulator = new DefaultProtocolManipulator(this);
        return this.defaultProtocolManipulator;
    }

    // Getters and Setters.

    public int getPassToProcessEvent() {
        return passToProcessEvent;
    }
}
