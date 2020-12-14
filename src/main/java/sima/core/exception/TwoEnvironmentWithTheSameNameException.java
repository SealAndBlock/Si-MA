package sima.core.exception;

public class TwoEnvironmentWithTheSameNameException extends RuntimeException {

    // Constructors.

    public TwoEnvironmentWithTheSameNameException() {
    }

    public TwoEnvironmentWithTheSameNameException(String message) {
        super(message);
    }

    public TwoEnvironmentWithTheSameNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoEnvironmentWithTheSameNameException(Throwable cause) {
        super(cause);
    }

    public TwoEnvironmentWithTheSameNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
