package sima.standard.environment.message;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.event.Event;
import sima.core.protocol.IntendedToProtocol;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Objects;
import java.util.Optional;

/**
 * A message is a particular event which has a content and which is intended to a specific protocol.
 * <p>
 * It is very important to implement the method {@link #equals(Object)} and {@link #hashCode()} because {@link Message} sometime must be duplicated
 * with the method {@link #duplicate()} and therefore, it is the must simple and correct way to identify a {@link Message} that we have already
 * received with these methods.
 */
public class Message extends Event implements IntendedToProtocol {

    // Variables.

    /**
     * The intended protocol.
     * <p>
     * It is the protocol which must treat the message.
     */
    private final ProtocolIdentifier intendedProtocol;

    // Constructors.

    /**
     * @param content          the content
     * @param intendedProtocol the intended protocol
     *
     * @throws IllegalArgumentException if intendedProtocol is null
     */
    public Message(Message content, ProtocolIdentifier intendedProtocol) {
        super(content);
        this.intendedProtocol = Optional.ofNullable(intendedProtocol).orElseThrow(() -> new IllegalArgumentException("IntendedProtocol cannot be " +
                                                                                                                             "null"));
    }

    private Message(Message message) {
        this(message.getContent().duplicate(), message.getIntendedProtocol());
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        if (!super.equals(o)) return false;
        return getIntendedProtocol().equals(message.getIntendedProtocol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIntendedProtocol());
    }

    @Override
    public @NotNull Message duplicate() {
        return new Message(this);
    }

    // Getters and Setters.

    /**
     * Same that {@link #getContent()}. Just here to be more clear that a {@link Message} contains other {@link Message}.
     *
     * @return the {@link #getContent()} cast in {@link Message}.
     */
    public Message getMessage() {
        return getContent();
    }

    @Override
    public Message getContent() {
        return (Message) super.getContent();
    }

    @Override
    public @NotNull ProtocolIdentifier getIntendedProtocol() {
        return intendedProtocol;
    }
}
