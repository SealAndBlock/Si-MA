package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;

/**
 * An transportable object.
 * <p>
 * An transportable object is an object which can be transport by being {@link Serializable}.
 * <p>
 * All sub classes which are not abstract must have a copy constructor to implement the duplicate methods.
 */
public interface Transportable extends Serializable {
    
    /**
     * Duplicate the transportable. A new instance must be created.
     * <p>
     * Sub classes must uses copy constructors or factory to implement this method.
     *
     * @return the clone of a object.
     */
    @NotNull Transportable duplicate();
    
    /**
     * @return the protocol for which the content is intended
     */
    ProtocolIdentifier getProtocolIntended();
    
    /**
     * @return true if the method {@link #getProtocolIntended()} returns not null {@link ProtocolIdentifier}
     */
    default boolean hasIntendedProtocol() {
        return getProtocolIntended() != null;
    }
    
}
