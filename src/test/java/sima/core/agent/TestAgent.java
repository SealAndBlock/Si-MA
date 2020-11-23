package sima.core.agent;

import sima.core.environment.event.Event;

import java.util.Map;

public class TestAgent extends AbstractAgent {

    // Variables.

    private int passToOnStart = 0;
    private int passToOnKill = 0;
    private int passToTreatNoProtocolEvent = 0;
    private int passToTreatEventWithNotFindProtocol = 0;
    private int passToProcessEvent = 0;

    // Constructors.

    public TestAgent(String agentName, int numberId, Map<String, String> args) {
        super(agentName, numberId, args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {

    }

    @Override
    public void onStart() {
        this.passToOnStart++;
    }

    @Override
    public void onKill() {
        this.passToOnKill++;
    }

    @Override
    public synchronized void processEvent(Event event) {
        super.processEvent(event);
        this.passToProcessEvent++;
    }

    @Override
    protected void treatNoProtocolEvent(Event event) {
        this.passToTreatNoProtocolEvent++;
    }

    @Override
    protected void treatEventWithNotFindProtocol(Event event) {
        this.passToTreatEventWithNotFindProtocol++;
    }

    public void reset() {
        this.passToOnStart = 0;
        this.passToOnKill = 0;
        this.passToTreatNoProtocolEvent = 0;
        this.passToTreatEventWithNotFindProtocol = 0;
        this.passToProcessEvent = 0;
    }

    // Getters and Setters.

    public int getPassToOnStart() {
        return passToOnStart;
    }

    public int getPassToOnKill() {
        return passToOnKill;
    }

    public int getPassToTreatNoProtocolEvent() {
        return passToTreatNoProtocolEvent;
    }

    public int getPassToTreatEventWithNotFindProtocol() {
        return passToTreatEventWithNotFindProtocol;
    }

    public int getPassToProcessEvent() {
        return passToProcessEvent;
    }
}
