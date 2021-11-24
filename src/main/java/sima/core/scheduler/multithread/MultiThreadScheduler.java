package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.ForcedWakeUpException;
import sima.core.exception.NotCorrectContextException;
import sima.core.scheduler.AbstractScheduler;
import sima.core.scheduler.executor.Executable;
import sima.core.scheduler.executor.MultiThreadExecutor;

import java.util.Optional;

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

    @Override
    public void scheduleAwait(Condition condition) throws ForcedWakeUpException, InterruptedException {
        prepareCondition(condition);
        awaitThread();
    }

    @Override
    public void scheduleAwait(Condition condition, long timeout) throws ForcedWakeUpException, InterruptedException {
        if (timeout >= NOW) {
            prepareCondition(condition);
            scheduleExecutableOnce(new WakeupExecutable(condition), timeout);
            awaitThread();
        } else
            throw new IllegalArgumentException("Timeout must be greater or equal to 1");
    }

    private void prepareCondition(Condition condition) {
        try {
            Optional.of(condition).get().prepare();
        } catch (ClassCastException e) {
            throw new NotCorrectContextException();
        }
    }

    private void awaitThread() throws InterruptedException, ForcedWakeUpException {
        MultiThreadExecutor.ExecutorThread eT = currentExecutorThread();
        eT.await();
    }

    private MultiThreadExecutor.ExecutorThread currentExecutorThread() {
        return (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
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
