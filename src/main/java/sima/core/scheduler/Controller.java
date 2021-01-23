package sima.core.scheduler;

import java.util.Map;

/**
 * A {@link Controller} is an {@link Executable}, however, this object is configure before the creation of the
 * simulation and can received argument. The configuration is done in the configuration file of the simulation.
 */
public abstract class Controller implements Executable {

    // Variables.

    // Constructors.

    protected Controller(Map<String, String> args) {
    }


}
