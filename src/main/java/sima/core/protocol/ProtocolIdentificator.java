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
public class ProtocolIdentificator implements Serializable {

    // Variables.

    /**
     * The name of the protocol.
     */
    private final String protocolName;

    // Constructors.

    /**
     * Constructs a {@link ProtocolIdentificator} with a protocol name.
     *
     * @param protocolName the protocol name (must be not null)
     * @throws NullPointerException if the protocolName is null
     */
    public ProtocolIdentificator(String protocolName) {
        this.protocolName = protocolName;
        if (this.protocolName == null)
            throw new NullPointerException();
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtocolIdentificator)) return false;
        ProtocolIdentificator that = (ProtocolIdentificator) o;
        return protocolName.equals(that.protocolName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolName);
    }

    // Getters and Setters.

    public String getProtocolName() {
        return protocolName;
    }
}
