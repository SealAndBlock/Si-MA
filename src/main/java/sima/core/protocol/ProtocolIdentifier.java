package sima.core.protocol;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A sima.core.protocol identifier allows the identification of a sima.core.protocol in a {@link sima.core.agent.SimaAgent}. If a {@link
 * sima.core.agent.SimaAgent} <i>A</i> has the same {@link Protocol} <i>P</i> than the {@link sima.core.agent.SimaAgent} <i>B</i>, then a {@link
 * Protocol} identifier <i>PI</i> for the {@link Protocol} <i>P</i> must allow the identification of the {@link Protocol} <i>P</i> in the {@link
 * sima.core.agent.SimaAgent} <i>A</i> and the {@link sima.core.agent.SimaAgent} <i>B</i>.
 * <p>
 * This object is {@link Serializable}, therefore all its attributes must be {@link Serializable} are used the key word
 * <i>transient</i>.
 * <p>
 * This class is immutable.
 */
public record ProtocolIdentifier(Class<? extends Protocol> protocolClass, String protocolTag)
        implements Serializable {
    
    // Constructors.
    
    /**
     * Constructs a {@link ProtocolIdentifier}.
     *
     * @param protocolClass the class of the protocol
     * @param protocolTag   the protocol tag.
     *
     * @throws NullPointerException if the protocolName and/or the {@link Protocol} tag is null
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
        if (!(o instanceof ProtocolIdentifier that)) return false;
        return Objects.equals(protocolClass, that.protocolClass) &&
                Objects.equals(protocolTag, that.protocolTag);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(protocolClass, protocolTag);
    }
}
