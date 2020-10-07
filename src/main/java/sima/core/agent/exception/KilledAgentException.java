package sima.core.agent.exception;

public class KilledAgentException extends RuntimeException {

    // Constructors.

    public KilledAgentException() {
    }

    public KilledAgentException(String message) {
        super(message);
    }

    public KilledAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public KilledAgentException(Throwable cause) {
        super(cause);
    }

    public KilledAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
