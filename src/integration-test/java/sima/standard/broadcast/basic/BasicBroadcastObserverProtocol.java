package sima.standard.broadcast.basic;

import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;
import sima.core.simulation.SimaSimulation;
import sima.standard.environment.message.StringMessage;

import java.util.Map;

import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.util.DataManager.AGENT_DELIVER_MESSAGE_PATH;
import static sima.util.DataManager.AGENT_NB_DELIVERY_PATH;
import static sima.util.DataManager.DataWriter.writeAgentId;
import static sima.util.DataManager.DataWriter.writeLine;

public class BasicBroadcastObserverProtocol extends Protocol {

    // Constructors.

    public BasicBroadcastObserverProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    // Methods.

    @Override
    public void processEvent(Event event) {
        if (event instanceof StringMessage stringMessage) {
            SimaLog.info(getAgentOwner().getAgentIdentifier() + " deliver the message " + stringMessage.getStringContent() + " at time " +
                                 SimaSimulation.getCurrentTime());
            writeAgentId(AGENT_DELIVER_MESSAGE_PATH, getAgentOwner());
            writeAgentId(AGENT_NB_DELIVERY_PATH, getAgentOwner());
        } else {
            throw new UnsupportedOperationException("Cannot treat other type of " + Event.class + " than " + StringMessage.class);
        }
    }

    @Override
    public void onOwnerStart() {
        // Nothing.
    }

    @Override
    public void onOwnerKill() {
        // Nothing.
    }

    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }

}
