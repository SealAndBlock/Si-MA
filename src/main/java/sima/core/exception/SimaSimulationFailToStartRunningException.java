package sima.core.exception;

public class SimaSimulationFailToStartRunningException extends Exception {

    // Constructors.

    public SimaSimulationFailToStartRunningException(Throwable throwable) {
        super(throwable);
    }

    public SimaSimulationFailToStartRunningException(String message, Throwable cause) {
        super(message, cause);
    }
}
