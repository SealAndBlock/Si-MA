package sima.core.exception;

public class TwoAgentWithSameIdentifierException extends RuntimeException {

    // Constructors.

    public TwoAgentWithSameIdentifierException() {
    }

    public TwoAgentWithSameIdentifierException(String message) {
        super(message);
    }

    public TwoAgentWithSameIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoAgentWithSameIdentifierException(Throwable cause) {
        super(cause);
    }

    public TwoAgentWithSameIdentifierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
