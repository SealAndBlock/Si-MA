package sima.core.protocol;

import sima.core.behavior.Behavior;

import java.util.Optional;

/**
 * Allows a {@link Protocol} to define several methods which can be control by the {@link ProtocolManipulator}. In that
 * way, it is possible to change the {@link Behavior} of a {@link Protocol} only by changing is current
 * {@link Protocol} manipulator and not by reimplement all the {@link Protocol}.
 */
public abstract class ProtocolManipulator {
    
    // Variables.
    
    /**
     * The manipulated {@link Protocol}.
     */
    private Protocol manipulatedProtocol;
    
    // Constructors.
    
    /**
     * Constructs a {@link ProtocolManipulator} with the instance of the {@link Protocol} which is manipulated by
     * him.
     *
     * @param manipulatedProtocol the new manipulated {@link Protocol} (must be not null)
     *
     * @throws NullPointerException if the manipulated {@link Protocol} is null
     */
    protected ProtocolManipulator(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol).get();
    }
    
    // Methods.
    
    /**
     * Reset the state of the {@link ProtocolManipulator}.
     * <p>
     * Use when the {@link #manipulatedProtocol} is changed in the method {@link Protocol#setProtocolManipulator(ProtocolManipulator)}.
     */
    public abstract void resetState();
    
    // Getters and Setters.
    
    public Protocol getManipulatedProtocol() {
        return manipulatedProtocol;
    }
    
    /**
     * @param manipulatedProtocol the new manipulated {@link Protocol} (must be not null)
     *
     * @throws NullPointerException if the manipulated {@link Protocol} is null
     */
    public void setManipulatedProtocol(Protocol manipulatedProtocol) {
        this.manipulatedProtocol = Optional.of(manipulatedProtocol).get();
    }
    
    // Inner class.
    
    public static class DefaultProtocolManipulator extends ProtocolManipulator {
        
        // Constructors.
        
        /**
         * Constructs a {@link ProtocolManipulator} with the instance of the {@link Protocol} which is manipulated
         * by him.
         *
         * @param manipulatedProtocol the new manipulated {@link Protocol} (must be not null)
         *
         * @throws NullPointerException if the manipulated {@link Protocol} is null
         */
        public DefaultProtocolManipulator(Protocol manipulatedProtocol) {
            super(manipulatedProtocol);
        }
        
        // Methods.
        
        @Override
        public void resetState() {
            // Nothing.
        }
    }
}
