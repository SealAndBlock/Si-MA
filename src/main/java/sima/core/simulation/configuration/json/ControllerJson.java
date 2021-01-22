package sima.core.simulation.configuration.json;

import java.util.List;

public class ControllerJson implements ArgumentativeObjectJson {

    // Variables.

    private String controllerClass;
    private String scheduleMode;
    private long beginAt;
    private long nbRepetitions;
    private long repetitionStep;
    private List<List<String>> args;

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
