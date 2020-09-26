package sima.core.behavior.exception;

public class BehaviorCannotBePlayedByAgentException extends BehaviorException {

    // Constructors.

    public BehaviorCannotBePlayedByAgentException() {
    }

    public BehaviorCannotBePlayedByAgentException(String message) {
        super(message);
    }

    public BehaviorCannotBePlayedByAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BehaviorCannotBePlayedByAgentException(Throwable cause) {
        super(cause);
    }

    public BehaviorCannotBePlayedByAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
