package sima.basic.environment.transport;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.utils.Box;

import java.util.Objects;

/**
 * {@link Transportable} for {@link String}. Because a String is immutable, the clone does not call the String copy constructor {@link
 * String#String(String)}.
 */
public class TransportableString implements Transportable, Box<String> {
    
    // Variables.
    
    private final String content;
    
    private final ProtocolIdentifier protocolTargeted;
    
    // Constructors.
    
    public TransportableString(String content) {
        this(content, null);
    }
    
    public TransportableString(String content, ProtocolIdentifier protocolTargeted) {
        this.content = content;
        this.protocolTargeted = protocolTargeted;
    }
    
    private TransportableString(TransportableString transportableString) {
        this.content = transportableString.content;
        this.protocolTargeted = transportableString.protocolTargeted;
    }
    
    // Methods.
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportableString that)) return false;
        return Objects.equals(getContent(), that.getContent()) && Objects.equals(getProtocolIntended(), that.getProtocolIntended());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getContent());
    }
    
    @Override
    public @NotNull TransportableString duplicate() {
        return new TransportableString(this);
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public ProtocolIdentifier getProtocolIntended() {
        return protocolTargeted;
    }
}
