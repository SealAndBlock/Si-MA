package sima.core.scheduler;

import java.util.Map;

public class ControllerTesting extends Controller {

    // Variables.

    private int passExecute;

    // Constructors.

    public ControllerTesting(Map<String, String> args) {
        super(args);
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
