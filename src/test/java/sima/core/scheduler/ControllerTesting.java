package sima.core.scheduler;

import java.util.Map;

public class ControllerTesting extends Controller {

    // Variables.

    private int passProcessArgument;
    private int passExecute;

    // Constructors.

    public ControllerTesting(Map<String, String> args) {
        super(args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
        passProcessArgument++;
    }

    @Override
    public void execute() {
        passExecute++;
    }

    // Getters and Setters.

    public int getPassProcessArgument() {
        return passProcessArgument;
    }

    public void setPassProcessArgument(int passProcessArgument) {
        this.passProcessArgument = passProcessArgument;
    }

    public int getPassExecute() {
        return passExecute;
    }

    public void setPassExecute(int passExecute) {
        this.passExecute = passExecute;
    }
}
