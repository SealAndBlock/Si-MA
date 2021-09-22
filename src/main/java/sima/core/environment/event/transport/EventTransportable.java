package sima.core.environment.event.transport;

import sima.core.utils.Duplicable;

import java.io.Serializable;

/**
 * An object which can be transported in an {@link sima.core.environment.event.Event}.
 * <p>
 * This object is {@link Serializable} for the next multi machine simulation features.
 */
public interface EventTransportable extends Duplicable<EventTransportable>, Serializable {
}
