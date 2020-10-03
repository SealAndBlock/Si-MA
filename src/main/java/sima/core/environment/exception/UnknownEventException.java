package sima.core.environment.exception;

public class UnknownEventException extends RuntimeException {

    // Constructors.

    public UnknownEventException() {
    }

    public UnknownEventException(String message) {
        super(message);
    }

    public UnknownEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownEventException(Throwable cause) {
        super(cause);
    }

    public UnknownEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
