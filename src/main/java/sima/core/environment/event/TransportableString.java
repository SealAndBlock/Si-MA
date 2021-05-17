package sima.core.environment.event;

import org.jetbrains.annotations.NotNull;
import sima.core.utils.Box;

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
    public @NotNull TransportableString duplicate() {
        return new TransportableString(this);
    }
    
    @Override
    public String getContent() {
        return content;
    }
}
