package sima.core.exception;

public class BehaviorException extends Exception {

    // Constructors.

    public BehaviorException() {
    }

    public BehaviorException(String message) {
        super(message);
    }

    public BehaviorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BehaviorException(Throwable cause) {
        super(cause);
    }

    public BehaviorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
