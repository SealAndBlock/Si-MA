package sima.standard.environment.message;

import org.jetbrains.annotations.NotNull;
import sima.core.protocol.ProtocolIdentifier;

import java.util.Objects;

public class StringMessage extends Message {

    // Variables.

    private final String stringContent;

    // Constructors.

    /**
     * @param stringContent    the string content
     * @param intendedProtocol the intended protocol
     *
     * @throws NullPointerException if intendedProtocol is null
     */
    public StringMessage(String stringContent, ProtocolIdentifier intendedProtocol) {
        super(null, intendedProtocol);
        this.stringContent = stringContent;
    }

    private StringMessage(StringMessage other) {
        this(other.stringContent, other.getIntendedProtocol());
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(stringContent, that.stringContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stringContent);
    }

    @Override
    public @NotNull StringMessage duplicate() {
        return new StringMessage(this);
    }

    // Getters.

    public String getStringContent() {
        return stringContent;
    }
}
