package sima.core.agent;

import sima.core.environment.event.Event;

import java.util.Map;

public class AgentTesting extends AbstractAgent {

    // Variables.

    private int passToProcessArgument;
    private int passToOnStart;
    private int passToOnKill;
    private int passToTreatNoProtocolEvent;
    private int passToTreatEventWithNotFindProtocol;
    private int passToProcessEvent;

    // Constructors.

    public AgentTesting(String agentName, int numberId, Map<String, String> args) {
        super(agentName, numberId, args);
    }

    // Methods.

    @Override
    protected void processArgument(Map<String, String> args) {
        this.passToProcessArgument++;
    }

    @Override
    public void onnStart() {
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

    // Getters and Setters.

    public int getPassToProcessArgument() {
        return this.passToProcessArgument;
    }

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
