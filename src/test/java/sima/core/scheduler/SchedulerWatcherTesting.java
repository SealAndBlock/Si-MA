package sima.core.scheduler;

public class SchedulerWatcherTesting implements Scheduler.SchedulerWatcher {

    // Variables.

    public int isPassToSchedulerStarted = 0;
    public int isPassToSchedulerKilled = 0;
    public int isPassToSimulationEndTimeReach = 0;
    public int isPassToNoExecutionToExecute = 0;

    // Methods.

    @Override
    public void schedulerStarted() {
        isPassToSchedulerStarted++;
    }

    @Override
    public void schedulerKilled() {
        isPassToSchedulerKilled++;
    }

    @Override
    public void simulationEndTimeReach() {
        isPassToSimulationEndTimeReach++;
    }

    @Override
    public void noExecutableToExecute() {
        isPassToNoExecutionToExecute++;
    }
}
