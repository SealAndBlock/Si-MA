package sima.testing.protocol;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class CorrectProtocol0 extends Protocol {

    // Variables.

    private int onOwnerStart;

    private int onOwnerKill;

    // Constructors.

    public CorrectProtocol0(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
        onOwnerStart = 0;
        onOwnerKill = 0;
    }

    // Methods.


    @Override
    public void onOwnerStart() {
        onOwnerStart++;
    }

    @Override
    public void onOwnerKill() {
        onOwnerKill++;
    }

    @Override
    public void processEvent(Event event) {
    }

    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }

    // Getters.

    public int getOnOwnerStart() {
        return onOwnerStart;
    }

    public int getOnOwnerKill() {
        return onOwnerKill;
    }
}
