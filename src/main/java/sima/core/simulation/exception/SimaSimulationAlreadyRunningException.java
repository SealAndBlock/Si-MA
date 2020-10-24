package sima.core.simulation.exception;

public class SimaSimulationAlreadyRunningException extends RuntimeException {

    // Constructors.

    public SimaSimulationAlreadyRunningException() {
    }

    public SimaSimulationAlreadyRunningException(String message) {
        super(message);
    }

    public SimaSimulationAlreadyRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimaSimulationAlreadyRunningException(Throwable cause) {
        super(cause);
    }

    public SimaSimulationAlreadyRunningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
