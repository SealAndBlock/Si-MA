package sima.core.environment.event;

import java.util.UUID;

/**
 * A {@link BroadcastGeneralEvent} is a {@link GeneralEvent} with no receiver. It is mean that the event is destined to
 * all agents of the environment.
 */
public abstract class BroadcastGeneralEvent extends GeneralEvent {

    // Constructors.

    /**
     * Constructs a {@link BroadcastGeneralEvent} which is a {@link GeneralEvent} with no receiver.
     *
     * @param sender the agent sender (cannot be null)
     */
    public BroadcastGeneralEvent(UUID sender) {
        super(sender, null);
    }
}
