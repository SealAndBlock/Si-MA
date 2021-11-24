package sima.core.scheduler;

import sima.core.scheduler.executor.Executable;

/**
 * A {@link Controller} is an {@link Executable}, however, this object is configure before the creation of the simulation and can receive argument.
 * Therefore, it must have a constructor with only Map(String, String) arg. The configuration is done in the configuration file of the simulation.
 */
public interface Controller extends Executable {
}
