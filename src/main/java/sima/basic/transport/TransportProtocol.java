package sima.basic.transport;

import sima.core.agent.SimpleAgent;
import sima.core.environment.Environment;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.Protocol;

import java.util.Map;

public abstract class TransportProtocol extends Protocol {
    
    // Variables.
    
    protected Environment environment;
    
    // Constructors.
    
    protected TransportProtocol(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    /**
     * @param transportable to process
     *
     * @throws UnsupportedOperationException TransportProtocol does not process transportable
     */
    @Override
    public void processTransportable(Transportable transportable) {
        throw new UnsupportedOperationException("By default a TransportProtocol does not process Transportable");
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
