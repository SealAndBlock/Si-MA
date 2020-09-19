package sima.core.agent.exception;

public class AlreadyStartedAgentException extends AgentException {

    // Constructors.

    public AlreadyStartedAgentException() {
    }

    public AlreadyStartedAgentException(String message) {
        super(message);
    }

    public AlreadyStartedAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyStartedAgentException(Throwable cause) {
        super(cause);
    }

    public AlreadyStartedAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
