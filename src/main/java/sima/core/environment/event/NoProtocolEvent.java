package sima.core.environment.event;

import java.util.UUID;

/**
 * Class which represents an {@link Event} with no protocol targeted.
 */
public abstract class NoProtocolEvent extends Event {

    // Constructors.

    /**
     * Constructs a {@link NoProtocolEvent}. A GeneralEvent is an {@link Event} with no protocol targeted.
     *
     * @param sender   the agent sender (cannot be null)
     * @param receiver the agent receiver
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
