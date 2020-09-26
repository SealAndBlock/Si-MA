package sima.core.environment.event;

import java.util.UUID;

/**
 * Class which represents an {@link Event} with no protocol targeted.
 */
public class GeneralEvent extends Event {

    // Constructors.

    /**
     * Constructs a {@link GeneralEvent}. A GeneralEvent is an {@link Event} with no protocol targeted.
     *
     * @param sender   the agent sender
     * @param receiver the agent receiver
     */
    public GeneralEvent(UUID sender, UUID receiver) {
        super(sender, receiver, null);
    }

    // Methods.

    /**
     * @return always returns true.
     */
    @Override
    public boolean isGeneralEvent() {
        return true;
    }
}
