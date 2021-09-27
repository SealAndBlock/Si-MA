package sima.basic.broadcast.reliable;

import org.jetbrains.annotations.NotNull;
import sima.basic.broadcast.basic.BroadcastMessage;
import sima.basic.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Objects;

public class ReliableBroadcastMessage extends BroadcastMessage {

    // Variables.

    /**
     * Used to make the difference between two {@link ReliableBroadcastMessage} which can be equals but they must be not because there are two
     * messages send at different times.
     */
    private final long numSequence;

    // Constructors.

    /**
     * @param numSequence      sequence of the message
     * @param sender           the message sender
     * @param content          the content
     * @param intendedProtocol the intended protocol
     *
     * @throws NullPointerException if the sender or the intended protocol is null
     */
    public ReliableBroadcastMessage(long numSequence, AgentIdentifier sender, Message content, ProtocolIdentifier intendedProtocol) {
        super(sender, content, intendedProtocol);
        this.numSequence = numSequence;
    }

    private ReliableBroadcastMessage(ReliableBroadcastMessage other) {
        this(other.numSequence, other.getSender(), other.getContent().duplicate(), other.getIntendedProtocol());
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReliableBroadcastMessage that)) return false;
        if (!super.equals(o)) return false;
        return numSequence == that.numSequence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numSequence);
    }

    @Override
    public @NotNull ReliableBroadcastMessage duplicate() {
        return new ReliableBroadcastMessage(this);
    }
}
