package sima.core.protocol;

/**
 * Allow a {@link Protocol} to define several methods which can be control by the {@link ProtocolManipulator}. In that
 * way, it is possible to change the behavior of a protocol only by changing is current protocol manipulator and not by
 * reimplement all the protocol.
 */
public interface ProtocolManipulator {

    /**
     * @param manipulatedProtocol the manipulated protocol
     */
    void setManipulatedProtocol(Protocol manipulatedProtocol);

}
