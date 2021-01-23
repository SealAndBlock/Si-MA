package sima.core.agent;

import sima.core.environment.event.Event;

import java.util.Map;

public class AgentTesting extends AbstractAgent {

    // Variables.

    private int passToOnStart;
    private int passToOnKill;
    private int passToProcessNoProtocolEvent;
    private int passToProcessEvent;

    // Constructors.

    public AgentTesting(String agentName, int numberId, Map<String, String> args) {
        super(agentName, numberId, args);
    }

    // Methods.

    @Override
    public void onnStart() {
        passToOnStart++;
    }

    @Override
    public void onKill() {
        passToOnKill++;
    }

    @Override
    public synchronized void processEvent(Event event) {
        super.processEvent(event);
        passToProcessEvent++;
    }

    @Override
    protected void processNoProtocolEvent(Event event) {
        passToProcessNoProtocolEvent++;
    }

    // Getters and Setters.

    public int getPassToOnStart() {
        return passToOnStart;
    }

    public int getPassToOnKill() {
        return passToOnKill;
    }

    public int getPassToProcessNoProtocolEvent() {
        return passToProcessNoProtocolEvent;
    }

    public int getPassToProcessEvent() {
        return passToProcessEvent;
    }
}
