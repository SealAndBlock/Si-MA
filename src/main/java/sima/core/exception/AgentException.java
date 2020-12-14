package sima.core.exception;

public class AgentException extends Exception {

    // Constructors.

    public AgentException() {
    }

    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgentException(Throwable cause) {
        super(cause);
    }

    public AgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
