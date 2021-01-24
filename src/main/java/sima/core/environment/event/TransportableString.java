package sima.core.environment.event;

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

    // Methods.

    @Override
    public TransportableString clone() {
        try {
            return (TransportableString) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    @Override
    public String getContent() {
        return content;
    }
}
