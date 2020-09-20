package sima.core.agent.exception;

public class AlreadyAddedProtocolException extends AgentException {

    // Constructors.

    public AlreadyAddedProtocolException() {
    }

    public AlreadyAddedProtocolException(String message) {
        super(message);
    }

    public AlreadyAddedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAddedProtocolException(Throwable cause) {
        super(cause);
    }

    public AlreadyAddedProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
