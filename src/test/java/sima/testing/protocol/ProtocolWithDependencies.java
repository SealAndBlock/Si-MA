package sima.testing.protocol;

import sima.core.agent.SimpleAgent;
import sima.core.environment.Environment;
import sima.core.environment.exchange.event.Event;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class ProtocolWithDependencies extends Protocol {
    
    // Variables.
    
    private Environment environment;
    private Protocol protocol;
    
    // Constructors
    
    public ProtocolWithDependencies(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    @Override
    public void processEvent(Event event) {
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
    
    // Getters and Setters.
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    public Protocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
    
    @Override
    public void processTransportable(Transportable transportable) {
    }
}
