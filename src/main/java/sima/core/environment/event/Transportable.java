package sima.core.environment.event;

import java.io.Serializable;

public interface Transportable extends Serializable, Cloneable {

    /**
     * Force sub class to implement the method clone.
     *
     * @return the clone of a object.
     * @see Object#clone()
     */
    Transportable clone();

}
