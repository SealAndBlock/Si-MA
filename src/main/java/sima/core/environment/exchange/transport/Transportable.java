package sima.core.environment.exchange.transport;

import sima.core.environment.exchange.ExchangeableObject;

import java.io.Serializable;

/**
 * A transportable object.
 * <p>
 * A transportable object is an object which can be transport by being {@link Serializable}.
 * <p>
 * All subclasses which are not abstract must have a copy constructor to implement the duplicate methods.
 */
public interface Transportable extends ExchangeableObject<Transportable> {
}
