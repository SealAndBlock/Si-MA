package sima.core.scheduler;

/**
 * Watcher which wait until that the {@code Scheduler} that it is watching is killed. Do nothing for
 * {@link #noExecutableToExecute()} and {@link #simulationEndTimeReach()}.
 */
public class WaitSchedulerWatcher implements Scheduler.SchedulerWatcher {

    // Variables.

    private final Object START_LOCK = new Object();
    private final Object KILL_LOCK = new Object();

    private int nbKill = 0;
    private int nbBlockKill = 0;

    // Methods.

    /**
     * Block until the next call of {@link Scheduler#kill()}.
     * <p>
     * If the {@code Scheduler} is already killed when the method is called, not block the thread.
     */
    public void waitUntilKilled() {
        synchronized (KILL_LOCK) {
            if (nbBlockKill == nbKill)
                try {
                    KILL_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            nbBlockKill = nbKill;
        }
    }

    @Override
    public void schedulerStarted() {
        synchronized (START_LOCK) {
            START_LOCK.notifyAll();
        }
    }

    @Override
    public void schedulerKilled() {
        synchronized (KILL_LOCK) {
            KILL_LOCK.notifyAll();
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
