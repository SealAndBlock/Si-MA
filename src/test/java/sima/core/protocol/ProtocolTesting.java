package sima.core.protocol;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;

import java.util.Map;

public class ProtocolTesting extends Protocol {

    // Variables.

    private int passToProcessEvent = 0;
    private int passToProcessArgument = 0;

    private DefaultProtocolManipulator defaultProtocolManipulator;

    // Constructors.

    public ProtocolTesting(String protocolTag, AbstractAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    // Methods.

    @Override
    public void processEvent(Event event) {
        this.passToProcessEvent++;
    }

    @Override
    protected void processArgument(Map<String, String> args) {
        this.passToProcessArgument++;
    }

    @Override
    protected ProtocolManipulator getDefaultProtocolManipulator() {
        if (this.defaultProtocolManipulator == null)
            this.defaultProtocolManipulator = new DefaultProtocolManipulator(this);
        return this.defaultProtocolManipulator;
    }

    public void reset() {
        this.passToProcessEvent = 0;
        this.passToProcessArgument = 0;
    }

    // Getters and Setters.

    public int getPassToProcessEvent() {
        return passToProcessEvent;
    }

    public int getPassToProcessArgument() {
        return passToProcessArgument;
    }

    // Inner class.

    private static class DefaultProtocolManipulator extends ProtocolManipulator {

        /**
         * Constructs a {@link ProtocolManipulator} with the instance of the the sima.core.protocol which is manipulated by him.
         *
         * @param manipulatedProtocol the new manipulated sima.core.protocol (must be not null)
         * @throws NullPointerException if the manipulated sima.core.protocol is null
         */
        public DefaultProtocolManipulator(Protocol manipulatedProtocol) {
            super(manipulatedProtocol);
        }
    }
}
