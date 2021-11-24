package sima.core.scheduler.executor;

@FunctionalInterface
public interface Executable {

    void execute();

    /**
     * Returns a non-null value if the current {@link Executable} is using the lock of the monitor.
     * <p>
     * For example in that case:
     * <pre>
     *  public void func() {
     *      final Object lockMonitor = new Object();
     *      synchronized(lockMonitor) {
     *          ...
     *          executable.execute();
     *          ...
     *      }
     *  }
     * </pre>
     * The method {@code getLockMonitor()} of the {@code Executable} must returns the reference of {@code lockMonitor} because the lockMonitor is used
     * during the call of the methods execute.
     *
     * @return an object which is the current lock monitor that the {@code Executable} is using.
     */
    default Object getLockMonitor() {
        return null;
    }

}
