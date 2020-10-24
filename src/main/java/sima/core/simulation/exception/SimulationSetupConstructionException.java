package sima.core.simulation.exception;

public class SimulationSetupConstructionException extends RuntimeException {

    // Constructors.

    public SimulationSetupConstructionException() {
    }

    public SimulationSetupConstructionException(String message) {
        super(message);
    }

    public SimulationSetupConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimulationSetupConstructionException(Throwable cause) {
        super(cause);
    }

    public SimulationSetupConstructionException(String message, Throwable cause, boolean enableSuppression,
                                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
