package sima.core.protocol;

import java.util.Optional;

/**
 * Allows a {@link Protocol} to define several methods which can be control by the {@link ProtocolManipulator}. In that
 * way, it is possible to change the sima.core.behavior of a sima.core.protocol only by changing is current sima.core.protocol manipulator and not by
 * reimplement all the sima.core.protocol.
 */
public abstract class ProtocolManipulator {

    // Variables.

    /**
     * The manipulated sima.core.protocol.
     */
    private Protocol manipulatedProtocol;

    // Constructors.

    /**
     * Constructs a {@link ProtocolManipulator} with the instance of the the sima.core.protocol which is manipulated by him.
     *
     * @param manipulatedProtocol the new manipulated sima.core.protocol (must be not null)
     * @throws NullPointerException if the manipulated sima.core.protocol is null
     */
    public ProtocolManipulator(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol).get();
    }

    // Methods.

    // Getters and Setters.

    public Protocol getManipulatedProtocol() {
        return manipulatedProtocol;
    }

    /**
     * @param manipulatedProtocol the new manipulated sima.core.protocol (must be not null)
     * @throws NullPointerException if the manipulated sima.core.protocol is null
     */
    public void setManipulatedProtocol(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol).get();
    }

    // Inner class.

    public static class DefaultProtocolManipulator extends ProtocolManipulator {

        /**
         * Constructs a {@link ProtocolManipulator} with the instance of the the sima.core.protocol which is manipulated by him.
         *
         * @param manipulatedProtocol the new manipulated sima.core.protocol (must be not null)
         * @throws NullPointerException if the manipulated sima.core.protocol is null
         */
        public DefaultProtocolManipulator(Protocol manipulatedProtocol) {
            super(manipulatedProtocol);
        }
    }
}
