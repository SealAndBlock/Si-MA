package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.ConfigurationException;
import sima.core.exception.FailInstantiationException;
import sima.core.scheduler.Controller;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.configuration.json.ControllerJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sima.core.simulation.configuration.parser.ConfigurationParser.parseArgs;
import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.instantiate;

public class ControllerParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;
    private final List<ControllerBundle> controllers;

    // Constructors.

    public ControllerParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = simaSimulationJson;
        controllers = new ArrayList<>();
    }

    // Methods.

    public void parseControllers() throws ConfigurationException, FailInstantiationException, ClassNotFoundException {
        controllers.clear();
        createAndAddControllers();
    }

    private void createAndAddControllers() throws ConfigurationException, FailInstantiationException, ClassNotFoundException {
        if (simaSimulationJson.hasControllers())
            for (ControllerJson controllerJson : simaSimulationJson.getControllers()) {
                var c = instantiateController(controllerJson.getControllerClass(), parseArgs(controllerJson));
                var cBundle = new ControllerBundle(c, Scheduler.ScheduleMode.valueOf(controllerJson.getScheduleMode()), controllerJson.getBeginAt(),
                                                   controllerJson.getNbRepetitions(), controllerJson.getRepetitionStep());
                controllers.add(cBundle);
            }
    }

    public void scheduleControllers(List<ControllerBundle> controllerBundles, Scheduler scheduler) {
        for (ControllerBundle controllerBundle : controllerBundles) {
            switch (controllerBundle.scheduleMode()) {
                case ONCE -> scheduler.scheduleExecutableOnce(controllerBundle.controller(), controllerBundle.beginAt());
                case REPEATED -> scheduler.scheduleExecutableRepeated(controllerBundle.controller(), controllerBundle.beginAt(),
                                                                      controllerBundle.nbRepetitions(), controllerBundle.repetitionStep());
                case INFINITE -> scheduler.scheduleExecutableInfinitely(controllerBundle.controller(), controllerBundle.beginAt(),
                                                                        controllerBundle.repetitionStep());
            }
        }
    }

    // Static.

    @NotNull
    public static Controller instantiateController(String controllerClassName, Map<String, String> args)
            throws ClassNotFoundException, FailInstantiationException {
        Class<? extends Controller> controllerClass = extractClassForName(controllerClassName);
        return instantiate(controllerClass, new Class[]{Map.class}, args);
    }

    // Getters.

    public List<ControllerBundle> getControllers() {
        return controllers;
    }

    // Inner classes.

    public record ControllerBundle(Controller controller, Scheduler.ScheduleMode scheduleMode, long beginAt, long nbRepetitions,
                                   long repetitionStep) {
    }
}
