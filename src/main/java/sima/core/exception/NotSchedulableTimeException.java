package sima.core.exception;

public class NotSchedulableTimeException extends RuntimeException {

    // Constructors.

    public NotSchedulableTimeException() {
    }

    public NotSchedulableTimeException(String message) {
        super(message);
    }

    public NotSchedulableTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSchedulableTimeException(Throwable cause) {
        super(cause);
    }

    public NotSchedulableTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
