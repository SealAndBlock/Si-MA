package sima.core.simulation.configuration.json;

import java.util.List;

public class ControllerJson implements ArgumentativeObjectJson {

    // Variables.

    private final String controllerClass;
    private final String scheduleMode;
    private final long beginAt;
    private final long nbRepetitions;
    private final long repetitionStep;
    private final List<List<String>> args;

    // Constructors.

    public ControllerJson(String controllerClass, String scheduleMode, long beginAt, long nbRepetitions,
                          long repetitionStep, List<List<String>> args) {
        this.controllerClass = controllerClass;
        this.scheduleMode = scheduleMode;
        this.beginAt = beginAt;
        this.nbRepetitions = nbRepetitions;
        this.repetitionStep = repetitionStep;
        this.args = args;
    }

    // Getters.

    public String getControllerClass() {
        return controllerClass;
    }

    public String getScheduleMode() {
        return scheduleMode;
    }

    public long getBeginAt() {
        return beginAt;
    }

    public long getNbRepetitions() {
        return nbRepetitions;
    }

    public long getRepetitionStep() {
        return repetitionStep;
    }

    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
