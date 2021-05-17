package sima.core.scheduler;

import java.util.Map;

public class ControllerTesting implements Controller {

    // Variables.

    private int passExecute;

    // Constructors.

    public ControllerTesting(Map<String, String> args) {
    }

    // Methods.

    @Override
    public void execute() {
        passExecute++;
    }

    // Getters and Setters.

    public int getPassExecute() {
        return passExecute;
    }

    public void setPassExecute(int passExecute) {
        this.passExecute = passExecute;
    }
}
