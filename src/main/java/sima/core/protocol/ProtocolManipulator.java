package sima.core.protocol;

import java.util.Optional;

/**
 * Allows a {@link Protocol} to define several methods which can be control by the {@link ProtocolManipulator}. In that
 * way, it is possible to change the behavior of a protocol only by changing is current protocol manipulator and not by
 * reimplement all the protocol.
 */
public abstract class ProtocolManipulator {

    // Variables.

    /**
     * The manipulated protocol.
     */
    private Optional<Protocol> manipulatedProtocol;

    // Constructors.

    /**
     * Constructs a {@link ProtocolManipulator} with the instance of the the protocol which is manipulated by him.
     *
     * @param manipulatedProtocol the new manipulated protocol (must be not null)
     * @throws NullPointerException if the manipulated protocol is null
     */
    public ProtocolManipulator(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol);
    }

    // Methods.

    // Getters and Setters.

    public Protocol getManipulatedProtocol() {
        return manipulatedProtocol.get();
    }

    /**
     * @param manipulatedProtocol the new manipulated protocol (must be not null)
     * @throws NullPointerException if the manipulated protocol is null
     */
    public void setManipulatedProtocol(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol);
    }
}
