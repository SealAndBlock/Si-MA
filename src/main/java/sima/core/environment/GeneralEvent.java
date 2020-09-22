package sima.core.environment;

import sima.core.agent.AbstractAgent;

public class GeneralEvent extends Event {

    // Constructors.

    public GeneralEvent(AbstractAgent sender, AbstractAgent receiver) {
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
