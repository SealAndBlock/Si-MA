package sima.core.protocol;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A sima.core.protocol identifier allows the identification of a sima.core.protocol in an sima.core.agent. If an sima
 * .core.agent <i>A</i> has the same sima.core.protocol <i>P</i> than the sima.core.agent <i>B</i>, then a
 * sima.core.protocol identifier <i>PI</i> for the sima.core.protocol <i>P</i> must allow the identification of the
 * sima.core.protocol <i>P</i> in the sima.core.agent <i>A</i> and the sima.core.agent <i>B</i>.
 * <p>
 * This object is {@link Serializable}, therefore all its attributes must be {@link Serializable} are used the key word
 * <i>transient</i>.
 */
public class ProtocolIdentifier implements Serializable {

    // Variables.

    /**
     * The name of the sima.core.protocol.
     */
    private final Class<? extends Protocol> protocolClass;

    /**
     * The tag of the sima.core.protocol.
     */
    private final String protocolTag;

    // Constructors.

    /**
     * Constructs a {@link ProtocolIdentifier}.
     *
     * @param protocolClass the class of the protocol
     * @param protocolTag   the protocol tag.
     * @throws NullPointerException if the protocolName and/or the sima.core.protocol tag is null
     */
    public ProtocolIdentifier(Class<? extends Protocol> protocolClass, String protocolTag) {
        this.protocolClass = Optional.of(protocolClass).get();
        this.protocolTag = Optional.of(protocolTag).get();
    }

    // Methods.

    @Override
    public String toString() {
        return "ProtocolIdentifier [" +
                "protocolClass=" + protocolClass +
                ", protocolTag=" + protocolTag + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtocolIdentifier)) return false;
        ProtocolIdentifier that = (ProtocolIdentifier) o;
        return Objects.equals(protocolClass, that.protocolClass) &&
                Objects.equals(protocolTag, that.protocolTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolClass, protocolTag);
    }

    // Getters and Setters.

    public Class<? extends Protocol> getProtocolClass() {
        return protocolClass;
    }

    public String getProtocolTag() {
        return protocolTag;
    }
}
