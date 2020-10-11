package sima.core.environment.event;

import java.util.UUID;

/**
 * Class which represents an {@link Event} with no sima.core.protocol targeted.
 */
public abstract class NoProtocolEvent extends Event {

    // Constructors.

    /**
     * Constructs a {@link NoProtocolEvent}. A GeneralEvent is an {@link Event} with no sima.core.protocol targeted.
     *
     * @param sender   the sima.core.agent sender (cannot be null)
     * @param receiver the sima.core.agent receiver
     */
    public NoProtocolEvent(UUID sender, UUID receiver) {
        super(sender, receiver, null);
    }

    // Methods.

    /**
     * @return always returns true.
     */
    @Override
    public boolean isNoProtocolEvent() {
        return true;
    }
}
