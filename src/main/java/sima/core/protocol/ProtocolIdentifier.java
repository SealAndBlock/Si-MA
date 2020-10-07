package sima.core.protocol;

import java.io.Serializable;
import java.util.Objects;

/**
 * A protocol identificator allows the identification of a protocol in an agent. If an agent <i>A</i> has the same
 * protocol <i>P</i> than the agent <i>B</i>, then a protocol identificator <i>PI</i> for the protocol <i>P</i> must
 * allow the identification of the protocol <i>P</i> in the agent <i>A</i> and the agent <i>B</i>.
 * <p>
 * This object is {@link Serializable}, therefore all its attributes must be {@link Serializable} are used the key word
 * <i>transient</i>.
 */
public class ProtocolIdentifier implements Serializable {

    // Variables.

    /**
     * The name of the protocol.
     */
    private final String protocolName;

    /**
     * The tag of the protocol.
     */
    private final String protocolTag;

    // Constructors.

    /**
     * Constructs a {@link ProtocolIdentifier} with a protocol name.
     *
     * @param protocolName the protocol name (must be not null)
     * @param protocolTag  the protocol tag (must be not null)
     * @throws NullPointerException if the protocolName and/or the protocol tag is null
     */
    public ProtocolIdentifier(String protocolName, String protocolTag) {
        this.protocolName = protocolName;
        this.protocolTag = protocolTag;

        if (this.protocolName == null || this.protocolTag == null)
            throw new NullPointerException();
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtocolIdentifier)) return false;
        ProtocolIdentifier that = (ProtocolIdentifier) o;
        return Objects.equals(protocolName, that.protocolName) &&
                Objects.equals(protocolTag, that.protocolTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolName, protocolTag);
    }

    // Getters and Setters.

    public String getProtocolName() {
        return protocolName;
    }

    public String getProtocolTag() {
        return protocolTag;
    }
}