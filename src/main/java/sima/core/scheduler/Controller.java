package sima.core.scheduler;

/**
 * A {@link Controller} is an {@link Executable}, however, this object is configure before the creation of the simulation and can received
 * argument. Therefore it must has a constructor with only Map(String, String) arg. The configuration is done in the configuration file of the
 * simulation.
 */
public interface Controller extends Executable {
}
