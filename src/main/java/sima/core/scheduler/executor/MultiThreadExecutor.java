package sima.core.scheduler.executor;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.ExecutorShutdownException;
import sima.core.exception.ForcedWakeUpException;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;

import static sima.core.simulation.SimaSimulation.SimaLog;

public class MultiThreadExecutor {

    // Locks.

    private final Object quiescenceLock = new Object();
    private final Object terminationLock = new Object();

    // Variables.

    private final int maxT;

    private final Deque<ExecutorThread> toExecuteThread;
    private final List<ExecutorThread> inExecutionThread;

    private final List<ExecutorThread> waitingThread;

    private boolean isShutdown = false;

    // Constructors.

    public MultiThreadExecutor(int maxT) {
        if (maxT > 0)
            this.maxT = maxT;
        else
            throw new IllegalArgumentException("maxT must be greater or equal to 1.");

        this.toExecuteThread = new LinkedList<>();
        this.inExecutionThread = new ArrayList<>();

        this.waitingThread = new ArrayList<>();
    }

    // Methods.

    /**
     * Executes the given command at some time in the future. The executor will try to respect the order of the method call.
     *
     * @param executable the executable to execute
     *
     * @throws RejectedExecutionException if the {@code MultiThreadExecutor} is shut down
     * @throws NullPointerException       if the executable is null
     */
    public synchronized void execute(Executable executable) {
        if (!isShutdown) {
            toExecuteThread.offerLast(createExecutorThread(Optional.of(executable).get()));
            tryExecuteThread();
        } else
            throw new RejectedExecutionException("The MultiThreadExecutor is shut down");
    }

    private ExecutorThread createExecutorThread(Executable executable) {
        ExecutorThread executorThread = new ExecutorThread(executable);
        executorThread.start();
        return executorThread;
    }

    /**
     * Try to execute new {@link ExecutorThread} which are in {@link #toExecuteThread}.
     */
    private synchronized void tryExecuteThread() {
        if (inExecutionThread.size() < maxT && !toExecuteThread.isEmpty()) {
            ExecutorThread executorThread = toExecuteThread.pollFirst();
            inExecutionThread.add(executorThread);
            executorThread.go();
        } else {
            shutdownProcedure();
            notifyAwaiter();
        }
    }

    private void notifyAwaiter() {
        if (isQuiescence()) {
            notifyQuiescenceAwaiter();

            if (isTerminated()) {
                notifyTerminationAwaiter();
            }
        }
    }

    private void shutdownProcedure() {
        if (isQuiescence() && isShutdown() && !waitingThread.isEmpty()) { // Normally pass one time.
            List<ExecutorThread> cloneWT = new ArrayList<>(waitingThread);
            for (ExecutorThread eT : cloneWT) {
                eT.forcedWakeUp();
            }
            tryExecuteThread();
        }
    }

    private void notifyQuiescenceAwaiter() {
        synchronized (quiescenceLock) {
            quiescenceLock.notifyAll();
        }
    }

    private void notifyTerminationAwaiter() {
        synchronized (terminationLock) {
            terminationLock.notifyAll();
        }
    }

    public synchronized boolean isShutdown() {
        return isShutdown;
    }

    /**
     * Shutdown the {@link MultiThreadExecutor}. After the call of this method, the method {@link #execute(Executable)} does not accept {@link
     * Executable} anymore and throws {@link RejectedExecutionException}.
     * <p>
     * This method continue the execution of all running {@code Executable}s and waiting to be executed {@code Executable}s. If at the end of the
     * executions of all {@code Executable}s, there are some waiting {@code Executables} (block by the method {@link ExecutorThread#await()}), the
     * method shutdown wakeup all theses {@code Executable}s with the method {@link ExecutorThread#forcedWakeUp()}. In that way, the method {@link
     * ExecutorThread#await()} will be unlocked and throws a {@link ForcedWakeUpException}.
     */
    public synchronized void shutdown() {
        if (!isShutdown) {
            isShutdown = true;
            tryExecuteThread();
        }
    }

    /**
     * Shutdown now the {@link MultiThreadExecutor}. After the call of this method, the method {@link #execute(Executable)} does not accept * {@link
     * Executable} anymore and throws {@link RejectedExecutionException}. All waiting to be executed {@link Executable}s are returns.
     * <p>
     * After this call, the {@code MultiThreadExecutor} will finish executing all {@code Executable}s which were already running. However, it is
     * possible that after that all {@code Executable} have finish their execution it always stays some waiting {@code Executable}s (block by the
     * method {@link ExecutorThread#await()}). In that case, the {@code MultiThreadExecutor} call for all theses {@code ExecutorThread}s the method
     * {@link ExecutorThread#forcedWakeUp()} which will do the same as {@link ExecutorThread#wakeUp()} excepted that the method {@link
     * ExecutorThread#await()} after the wakeup will throw a {@link ForcedWakeUpException}.
     *
     * @return a list which contains all not executed {@link Executable}. Never null.
     */
    public @NotNull List<Executable> shutdownNow() {
        shutdown();
        List<Executable> notExecuted;
        synchronized (this) {
            interruptNoExecutedThread();
            notExecuted = getNotExecuted();
            toExecuteThread.clear();
        }
        return notExecuted;
    }

    private void interruptNoExecutedThread() {
        toExecuteThread.forEach(Thread::interrupt);
    }

    @NotNull
    private List<Executable> getNotExecuted() {
        return new ArrayList<>(toExecuteThread.stream().map(executorThread -> executorThread.executable).toList());
    }

    public synchronized boolean isQuiescence() {
        return (inExecutionThread.isEmpty() && toExecuteThread.isEmpty());
    }

    public boolean awaitQuiescence() throws InterruptedException {
        while (!isQuiescence()) {
            synchronized (quiescenceLock) {
                quiescenceLock.wait();
            }
        }

        return isQuiescence();
    }

    public boolean awaitQuiescence(long timeout) throws InterruptedException {
        if (!isQuiescence()) {
            synchronized (quiescenceLock) {
                quiescenceLock.wait(timeout);
            }
        }

        return isQuiescence();
    }

    /**
     * @return true if the {@link MultiThreadExecutor} is shutdown and all {@link Executable} has been executed.
     */
    public synchronized boolean isTerminated() {
        return isQuiescence() && isShutdown;
    }

    /**
     * Wait until the {@link MultiThreadExecutor} terminate only after a shutdown call.
     *
     * @param timeout the timeout to wait the termination
     *
     * @return true if the {@code MultiThreadExecutor} is terminated after the timeout, else false.
     *
     * @throws InterruptedException if the thread is interrupted during the wait
     */
    public boolean awaitTermination(long timeout) throws InterruptedException {
        if (isShutdown()) {
            if (!isTerminated())
                synchronized (terminationLock) {
                    terminationLock.wait(timeout);
                }
            return isTerminated();

        } else
            return false;
    }

    // Inner classes.

    public class ExecutorThread extends Thread {

        // Variables.

        private final Object barrier;

        private final Object lockMonitor;

        private boolean waiting = false;

        private boolean canBeExecuted = false;

        private boolean hasBeenWakeUp = false;

        private boolean forcedWakeUp = false;

        private final Executable executable;

        private final MultiThreadExecutor executor;

        // Constructors.

        private ExecutorThread(Executable executable) {
            super();

            this.barrier = new Object();
            this.lockMonitor = new Object();

            this.executable = executable;
            this.executor = MultiThreadExecutor.this;
        }

        // Methods.

        @Override
        public void run() {
            try {
                waitUntilGo();
                execute();
            } catch (InterruptedException e) {
                SimaLog.error("ExecutorThread interrupted during waiting in the barrier -> Execution of the executable " + executable + " does not " +
                                      "occur", e);
                Thread.currentThread().interrupt();
            } finally {
                setFinished();
            }
        }

        private void waitUntilGo() throws InterruptedException {
            synchronized (barrier) {
                while (!canBeExecuted)
                    barrier.wait();
            }
        }

        private void execute() {
            try {
                executable.execute();
            } catch (Exception e) {
                SimaLog.error(String.format("Execution of the executable %s FAILED", executable), e);
            }
        }

        private void go() {
            unlockBarrier();
            afterWakeUp();
        }

        private void unlockBarrier() {
            if (!canBeExecuted) { // Never start the execution.
                synchronized (barrier) {
                    canBeExecuted = true;
                    barrier.notifyAll();
                }
            }
        }

        private void afterWakeUp() {
            synchronized (getLockMonitor()) {
                if (hasBeenWakeUp) {
                    getLockMonitor().notifyAll();
                }
            }
        }

        /**
         * Block the current thread until another thread call methods {@link #wakeUp()} or {@link #forcedWakeUp()}.
         *
         * @throws InterruptedException  if the current thread is interrupted
         * @throws ForcedWakeUpException if the wakeup has been done with the method {@link #forcedWakeUp()}
         * @see #wakeUp()
         * @see #forcedWakeUp()
         */
        public void await() throws InterruptedException, ForcedWakeUpException {
            if (!isShutdown()) {
                synchronized (getLockMonitor()) {
                    notifyWait();
                    waitOnLockMonitor();
                    throwsIfForcedWakeup();
                }
            } else
                throw new ExecutorShutdownException("The MultiThreadExecutor is shutdown -> it is not possible to wait in that state");
        }

        private void notifyWait() {
            synchronized (executor) {
                inExecutionThread.remove(this);
            }
        }

        private void waitOnLockMonitor() throws InterruptedException {
            synchronized (getLockMonitor()) {
                waitingThread.add(this);
                while (!hasBeenWakeUp) {
                    waiting = true;
                    getLockMonitor().wait();
                    waiting = false;
                }

                hasBeenWakeUp = false;
            }
        }

        private void throwsIfForcedWakeup() throws ForcedWakeUpException {
            if (forcedWakeUp) {
                forcedWakeUp = false;
                throw new ForcedWakeUpException();
            }
        }

        public void wakeUp() {
            synchronized (getLockMonitor()) {
                if (waiting) {
                    hasBeenWakeUp = true;
                    notifyWakeUp();
                }
            }
        }

        private void forcedWakeUp() {
            synchronized (getLockMonitor()) {
                forcedWakeUp = true;
                wakeUp();
            }
        }

        private void notifyWakeUp() {
            synchronized (executor) {
                waitingThread.remove(this);
                toExecuteThread.offerLast(this);
                tryExecuteThread();
            }
        }

        // Getters and Setters.

        private Object getLockMonitor() {
            return executable.getLockMonitor() == null ? lockMonitor : executable.getLockMonitor();
        }

        private void setFinished() {
            synchronized (executor) {
                inExecutionThread.remove(this);
                tryExecuteThread();
            }
        }
    }

}
