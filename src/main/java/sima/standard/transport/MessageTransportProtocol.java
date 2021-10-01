package sima.standard.transport;

import sima.standard.environment.message.Message;
import sima.standard.environment.message.MessageReceiver;
import sima.standard.environment.message.event.physical.PhysicalMessageReceptionEvent;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.NoPhysicalConnectionLayerFoundException;
import sima.core.protocol.Protocol;

import java.util.Map;
import java.util.Optional;

public abstract class MessageTransportProtocol extends Protocol implements MessageReceiver {
    
    // Static.
    
    public static final String ARG_PHYSICAL_CONNECTION_LAYER_NAME = "physicalConnectionLayerName";
    
    // Variables.
    
    private Environment environment;
    
    private String physicalConnectionLayerName;
    
    // Constructors.
    
    /**
     * Create a {@link Protocol} which use especially a {@link sima.core.environment.physical.PhysicalConnectionLayer} of an {@link Environment}
     * to transport {@link Message}
     *
     * @param protocolTag the protocol tag
     * @param agentOwner  the agent owner
     * @param args        the arguments
     *
     * @throws IllegalArgumentException if there is no {@link #ARG_PHYSICAL_CONNECTION_LAYER_NAME} argument in args.
     */
    protected MessageTransportProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
        parseArgs(args);
    }
    
    // Methods.
    
    /**
     * Search the argument with the key {@link #ARG_PHYSICAL_CONNECTION_LAYER_NAME}.
     *
     * @param args the arguments
     */
    private void parseArgs(Map<String, String> args) {
        if (args == null)
            throw new IllegalArgumentException(
                    MessageTransportProtocol.class + " must have one argument call " + ARG_PHYSICAL_CONNECTION_LAYER_NAME);
        
        physicalConnectionLayerName = args.get(ARG_PHYSICAL_CONNECTION_LAYER_NAME);
        if (physicalConnectionLayerName == null)
            throw new IllegalArgumentException("No " + ARG_PHYSICAL_CONNECTION_LAYER_NAME + " argument");
    }
    
    private PhysicalMessageReceptionEvent createMessageReception(Message message) {
        return new PhysicalMessageReceptionEvent(message, getIdentifier());
    }
    
    /**
     * Transport the {@link Message} to the target.
     * <p>
     * Take the linked {@link Environment} and get the {@link sima.core.environment.physical.PhysicalConnectionLayer} specified in args during
     * the initialisation of the object ({@link #MessageTransportProtocol(String, SimaAgent, Map)}).
     *
     * @param target  the target to transport message
     * @param message the message to transport
     *
     * @throws IllegalArgumentException if target is null
     */
    public void send(AgentIdentifier target, Message message) {
        target = Optional.ofNullable(target).orElseThrow(() -> new IllegalArgumentException("The target cannot be null"));
        var physicalConnectionLayer = getEnvironment().getPhysicalConnectionLayer(physicalConnectionLayerName);
        if (physicalConnectionLayer != null)
            physicalConnectionLayer.send(getAgentOwner().getAgentIdentifier(), target, createMessageReception(message));
        else
            throw new NoPhysicalConnectionLayerFoundException("The environment " + getEnvironment() + " does not have a " +
                    PhysicalConnectionLayer.class + " mapped with the name " + physicalConnectionLayerName);
    }
    
    @Override
    public void processEvent(Event event) {
        if (event instanceof PhysicalMessageReceptionEvent physicalMessageReceptionEvent) {
            receive(physicalMessageReceptionEvent.getContent());
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support other " + Event.class + " than " + PhysicalMessageReceptionEvent.class);
    }
    
    // Getters and Setters.
    
    public Environment getEnvironment() {
        return environment;
    }
    
    /**
     * Can be use one times at the initialisation of the simulation by using the field "protocolDependencies" in the json configuration file.
     *
     * @param environment the environment to set
     */
    public void setEnvironment(Environment environment) {
        if (this.environment == null && environment != null)
            this.environment = environment;
    }
}
