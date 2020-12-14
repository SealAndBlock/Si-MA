package sima.core.exception;

public class AgentNotStartedException extends RuntimeException {

    // Constructors.

    public AgentNotStartedException() {
    }

    public AgentNotStartedException(String message) {
        super(message);
    }

    public AgentNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgentNotStartedException(Throwable cause) {
        super(cause);
    }

    public AgentNotStartedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
