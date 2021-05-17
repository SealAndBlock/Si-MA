package sima.core.scheduler;

import sima.core.simulation.SimaSimulation;

/**
 * Watcher which wait until that the {@code Scheduler} that it is watching is killed. Do nothing for {@link
 * #noExecutableToExecute()} and {@link #simulationEndTimeReach()}.
 */
public class WaitSchedulerWatcher implements Scheduler.SchedulerWatcher {
    
    // Variables.
    
    private final Object startLock = new Object();
    private final Object killLock = new Object();
    
    private int nbKill = 0;
    private int nbBlockKill = 0;
    
    // Methods.
    
    /**
     * Block until the next call of {@link Scheduler#kill()}.
     * <p>
     * If the {@code Scheduler} is already killed when the method is called, not block the thread.
     */
    public void waitUntilKilled() {
        synchronized (killLock) {
            while (nbBlockKill == nbKill)
                try {
                    killLock.wait();
                } catch (InterruptedException e) {
                    SimaSimulation.SimaLog.error("Interrupt during waiting", e);
                    Thread.currentThread().interrupt();
                    return;
                }
            nbBlockKill = nbKill;
        }
    }
    
    @Override
    public void schedulerStarted() {
        synchronized (startLock) {
            startLock.notifyAll();
        }
    }
    
    @Override
    public void schedulerKilled() {
        synchronized (killLock) {
            killLock.notifyAll();
            nbKill++;
        }
    }
    
    @Override
    public void simulationEndTimeReach() {
        // Nothing.
    }
    
    @Override
    public void noExecutableToExecute() {
        // Nothing.
    }
}
