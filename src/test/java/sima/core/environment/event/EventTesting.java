package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

public class EventTesting extends Event {
    
    /**
     * Constructs an {@link Event} with the sima.core.agent which sends the event, the sima.core.agent which will
     * receive the event, and the class of the sima.core.protocol which must process the event.
     * <p>
     * The sender cannot be null, if it is the case a {@link NullPointerException} is thrown.
     * <p>
     * The receiver can be null, in that case it is to the sima.core.environment to manage which sima.core.agent(s) are
     * the receivers of the {@code Event}.
     *
     * @param sender           the sima.core.agent sender (cannot be null)
     * @param receiver         the sima.core.agent receiver
     * @param protocolTargeted the sima.core.protocol targeted
     *
     * @throws NullPointerException if the sender is null
     */
    public EventTesting(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted) {
        super(sender, receiver, protocolTargeted);
    }
    
    // Methods.
    
    @Override
    public @NotNull EventTesting duplicate() {
        return new EventTesting(this.getSender(), this.getReceiver(), this.getProtocolTargeted());
    }
    
    @Override
    public @NotNull Event duplicateWithNewReceiver(AgentIdentifier newReceiver) {
        return new EventTesting(this.getSender(), newReceiver, this.getProtocolTargeted());
    }
}
