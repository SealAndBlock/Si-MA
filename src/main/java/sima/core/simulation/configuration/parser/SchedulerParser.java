package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.FailInstantiationException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.instantiate;

public class SchedulerParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;

    private Scheduler scheduler;

    // Constructors.

    public SchedulerParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = simaSimulationJson;
        scheduler = null;
    }

    // Methods.

    public void parseScheduler() throws FailInstantiationException, ClassNotFoundException {
        scheduler = null;
        createScheduler();
        addSchedulerWatcher();
    }

    private void createScheduler() {
        Scheduler.TimeMode timeMode = Scheduler.TimeMode.valueOf(simaSimulationJson.getTimeMode());
        Scheduler.SchedulerType schedulerType = Scheduler.SchedulerType.valueOf(simaSimulationJson.getSchedulerType());
        if (timeMode == Scheduler.TimeMode.REAL_TIME)
            scheduler = createRealTimeScheduler(schedulerType, simaSimulationJson.getNbThreads(), simaSimulationJson.getEndTime());
        else
            scheduler = createDiscreteTimeScheduler(schedulerType, simaSimulationJson.getNbThreads(), simaSimulationJson.getEndTime());
    }

    private @NotNull Scheduler createRealTimeScheduler(Scheduler.SchedulerType simulationSchedulerType, int nbExecutorThread, long endSimulation) {
        throw new UnsupportedOperationException("Real Time is not supported in this version");
    }

    private @NotNull Scheduler createDiscreteTimeScheduler(Scheduler.SchedulerType simulationSchedulerType, int nbExecutorThread,
                                                           long endSimulation) {
        if (simulationSchedulerType == Scheduler.SchedulerType.MONO_THREAD)
            throw new UnsupportedOperationException("Discrete Time Mono thread simulation unsupported.");

        return new DiscreteTimeMultiThreadScheduler(endSimulation, nbExecutorThread);
    }

    private void addSchedulerWatcher() throws FailInstantiationException, ClassNotFoundException {
        Scheduler.SchedulerWatcher schedulerWatcher = createSchedulerWatcher(simaSimulationJson.getSchedulerWatcherClass());
        if (schedulerWatcher != null)
            scheduler.addSchedulerWatcher(schedulerWatcher);
    }

    private Scheduler.SchedulerWatcher createSchedulerWatcher(String schedulerWatcherClassName)
            throws ClassNotFoundException, FailInstantiationException {
        if (schedulerWatcherClassName != null && !schedulerWatcherClassName.isEmpty())
            return instantiateSchedulerWatcher(extractClassForName(schedulerWatcherClassName));
        else
            return null;
    }

    // Static

    public static @NotNull Scheduler.SchedulerWatcher instantiateSchedulerWatcher(Class<? extends Scheduler.SchedulerWatcher> schedulerWatcherClass)
            throws FailInstantiationException {
        return instantiate(schedulerWatcherClass);
    }

    // Getters.

    public Scheduler getScheduler() {
        return scheduler;
    }
}
