package sima.core.simulation;

public interface SimSetup {

    /**
     * Called when the simulation is started and must be setup.
     * <p>
     * When this method is called, all agents defined in the configuration file has already been added in the
     * simulation.
     * <p>
     * In this method you can add more agents which are not define in the simulation and you must create some event
     * for that the simulation begin, else the simulation will stop directly after its start because there is no
     * event to execute.
     * <p>
     * This method also allows that the simulation can start without configuration file. In that case it is in this
     * method that the configuration is done. However it is not recommended except if you know how the simulation
     * works.
     */
    void setupSimulation();

}
