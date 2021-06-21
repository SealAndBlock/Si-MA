package sima.testing.scheduler;

import sima.core.scheduler.Scheduler;

public class SimpleSchedulerWatcher implements Scheduler.SchedulerWatcher {
    
    // Methods.
    
    @Override
    public void schedulerStarted() {
    }
    
    @Override
    public void schedulerKilled() {
    }
    
    @Override
    public void simulationEndTimeReach() {
    }
    
    @Override
    public void noExecutableToExecute() {
    }
    
}
