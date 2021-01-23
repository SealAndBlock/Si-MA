package sima.core.simulation.specific;

import sima.core.scheduler.ControllerTesting;

import java.util.Map;

public class SpecificControllerTesting extends ControllerTesting {

    // Static.

    public static int PASS_EXECUTE = 0;

    // Constructors.

    public SpecificControllerTesting(Map<String, String> args) {
        super(args);
    }

    // Methods.

    @Override
    public void execute() {
        super.execute();
        PASS_EXECUTE++;
    }
}
