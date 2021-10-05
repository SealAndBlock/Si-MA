package sima.standard.transport.message;

import sima.core.agent.SimaAgent;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.ProtocolManipulator;
import sima.standard.environment.message.Message;
import sima.standard.transport.MessageTransportProtocol;

import java.util.Map;

/**
 * This class is a {@link MessageTransportProtocol} which simple send {@link Message}.
 * <p>
 * A {@link SimpleMessageSenderProtocol} just send {@link Message} without take care about if the message will arrive or not to the target.
 * <p>
 * There is no guarantee on the message reception and there is no guarantee on the message deliver order.
 */
public class SimpleMessageSenderProtocol extends MessageTransportProtocol {

    // Constructors

    public SimpleMessageSenderProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    @Override
    public void onOwnerStart() {
        // Nothing.
    }

    @Override
    public void onOwnerKill() {
        // Nothing.
    }

    // Methods.

    @Override
    public void receive(Message message) {
        deliver(message);
    }

    @Override
    public void deliver(Message message) {
        var content = message.getMessage();
        if (content != null) {
            var intendedProtocolId = content.getIntendedProtocol();
            var intendedProtocol = getAgentOwner().getProtocol(intendedProtocolId);
            if (intendedProtocol != null)
                intendedProtocol.processEvent(content);
            else
                throw new UnknownProtocolForAgentException(
                        "The agent " + getAgentOwner() + " does not know the intendedProtocol identify by " + intendedProtocolId);
        } else
            throw new UnsupportedOperationException("Cannot transfer content to intended protocol because message content is null");
    }

    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
}
