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

    public AgentTesting(String agentName, int sequenceId, int uniqueId, Map<String, String> args) {
        super(agentName, sequenceId, uniqueId, args);
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
    protected void inProcessEvent(Event event) {
        super.inProcessEvent(event);
        passToProcessEvent++;
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
