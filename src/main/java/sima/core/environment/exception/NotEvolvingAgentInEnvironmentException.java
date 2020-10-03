package sima.core.environment.exception;

public class NotEvolvingAgentInEnvironmentException extends Exception {

    // Constructors.

    public NotEvolvingAgentInEnvironmentException() {
    }

    public NotEvolvingAgentInEnvironmentException(String message) {
        super(message);
    }

    public NotEvolvingAgentInEnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEvolvingAgentInEnvironmentException(Throwable cause) {
        super(cause);
    }

    public NotEvolvingAgentInEnvironmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
