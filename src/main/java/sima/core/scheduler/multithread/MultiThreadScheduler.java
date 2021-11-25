package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.scheduler.AbstractScheduler;
import sima.core.scheduler.executor.Executable;
import sima.core.scheduler.executor.MultiThreadExecutor;

public abstract class MultiThreadScheduler extends AbstractScheduler {

    // Variables.

    /**
     * The number of thread used to execute all {@link Executable}.
     */
    protected final int nbExecutorThread;

    protected MultiThreadExecutor executor;

    // Constructors.

    protected MultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        super(endSimulation);

        this.nbExecutorThread = nbExecutorThread;
        if (nbExecutorThread < 1)
            throw new IllegalArgumentException("The number of executor thread must be greater or equal to 1.");
    }

    // Methods.

    @Override
    public String toString() {
        return "MultiThreadScheduler{" +
                "isStarted=" + isStarted +
                ", isKilled=" + isKilled +
                ", nbExecutorThread=" + nbExecutorThread +
                ", executor=" + executor +
                '}';
    }

    /**
     * Instantiates {@link #executor}.
     */
    protected abstract void createNewExecutor();

    /**
     * Shutdown the executor and set {@link #executor} to null.
     */
    protected void shutdownExecutor() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    @Override
    public @NotNull SchedulerType getSchedulerType() {
        return SchedulerType.MULTI_THREAD;
    }
}
