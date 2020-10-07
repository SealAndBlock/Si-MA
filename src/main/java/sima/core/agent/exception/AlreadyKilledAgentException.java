package sima.core.agent.exception;

public class AlreadyKilledAgentException extends RuntimeException {

    // Constructors.

    public AlreadyKilledAgentException() {
    }

    public AlreadyKilledAgentException(String message) {
        super(message);
    }

    public AlreadyKilledAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyKilledAgentException(Throwable cause) {
        super(cause);
    }

    public AlreadyKilledAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
