package sima.basic.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.event.Transportable;
import sima.core.utils.Box;

import java.util.Objects;

/**
 * {@link Transportable} for {@link String}. Because a String is immutable, the clone does not call the String copy
 * constructor {@link String#String(String)}.
 */
public class TransportableString implements Transportable, Box<String> {
    
    // Variables.
    
    private final String content;
    
    // Constructors.
    
    public TransportableString(String content) {
        this.content = content;
    }
    
    private TransportableString(TransportableString transportableString) {
        this.content = transportableString.content;
    }
    
    // Methods.
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportableString)) return false;
        TransportableString that = (TransportableString) o;
        return Objects.equals(getContent(), that.getContent());
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
}
