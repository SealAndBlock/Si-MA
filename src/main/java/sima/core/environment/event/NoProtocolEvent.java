package sima.core.environment.event;

import sima.core.agent.AgentIdentifier;

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
    public NoProtocolEvent(AgentIdentifier sender, AgentIdentifier receiver) {
        super(sender, receiver, null);
    }

    // Methods.

    /**
     * @return always returns true.
     */
    @Override
    public boolean isProtocolEvent() {
        return true;
    }
}
