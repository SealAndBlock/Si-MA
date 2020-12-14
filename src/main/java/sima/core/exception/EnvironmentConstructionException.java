package sima.core.exception;

public class EnvironmentConstructionException extends RuntimeException {

    // Constructors.

    public EnvironmentConstructionException() {
    }

    public EnvironmentConstructionException(String message) {
        super(message);
    }

    public EnvironmentConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnvironmentConstructionException(Throwable cause) {
        super(cause);
    }

    public EnvironmentConstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
