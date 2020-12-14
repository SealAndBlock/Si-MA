package sima.core.exception;

public class UnknownAgentException extends RuntimeException {

    // Constructors.

    public UnknownAgentException() {
    }

    public UnknownAgentException(String message) {
        super(message);
    }

    public UnknownAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownAgentException(Throwable cause) {
        super(cause);
    }

    public UnknownAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
