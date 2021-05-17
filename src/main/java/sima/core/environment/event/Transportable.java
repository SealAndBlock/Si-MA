package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;

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
     * Duplicate the transportable.
     * <p>
     * Sub classes must uses copy constructors or factory to implement this method.
     *
     * @return the clone of a object.
     */
    @NotNull Transportable duplicate();
    
}
