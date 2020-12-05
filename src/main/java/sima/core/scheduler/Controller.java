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
        if (args != null)
            this.processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments map (map argument name with the argument)
     */
    protected abstract void processArgument(Map<String, String> args);

}
