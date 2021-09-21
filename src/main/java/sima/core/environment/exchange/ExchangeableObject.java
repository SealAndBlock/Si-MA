package sima.core.environment.exchange;

import org.jetbrains.annotations.NotNull;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;

/**
 * Object which can be exchange between {@link sima.core.protocol.Protocol} of different {@link sima.core.agent.SimpleAgent}. This object is
 * {@link Serializable} because in a local simulation object does not need to be sent via network, but it can be the case when the several
 * machine simulation will be implemented.
 * <p>
 * An {@link ExchangeableObject} has a protocolIntended which is the protocol which must process the object when it is received.
 */
public interface ExchangeableObject<T> extends Serializable {
    
    /**
     * Duplicate the {@link ExchangeableObject}. A new instance must be created.
     * <p>
     * Sub classes must uses copy constructors or factory to implement this method.
     *
     * @return a clone of the object.
     */
    @NotNull T duplicate();
    
    /**
     * @return the protocol for which the {@link ExchangeableObject} is intended
     */
    ProtocolIdentifier getProtocolIntended();
    
    /**
     * @return true if the method {@link #getProtocolIntended()} returns not null {@link ProtocolIdentifier}
     */
    default boolean hasIntendedProtocol() {
        return getProtocolIntended() != null;
    }
    
}
