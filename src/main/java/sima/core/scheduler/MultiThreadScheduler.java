package sima.core.scheduler;

public class MultiThreadScheduler implements Scheduler{

    // Variables.

    // Constructors.

    // Methods.

    @Override
    public boolean addSchedulerWatcher(SchedulerWatcher schedulerWatcher) {
        return false;
    }

    @Override
    public void removeSchedulerWatcher(SchedulerWatcher schedulerWatcher) {

    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean kill() {
        return false;
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode, long nbRepetitions, long executionTimeStep) {

    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {

    }

    @Override
    public long getCurrentTime() {
        return 0;
    }
}
