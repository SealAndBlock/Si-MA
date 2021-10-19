package sima.standard.broadcast.basic.controller;

import sima.core.agent.SimaAgent;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.scheduler.Controller;
import sima.core.simulation.SimaSimulation;
import sima.standard.broadcast.basic.BasicBroadcastObserverProtocol;
import sima.standard.broadcast.basic.BasicBroadcast;
import sima.standard.environment.message.StringMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.util.DataManager.DataReader.getCorrectAgent;

/**
 * Search a correct {@link SimaAgent} and make that this {@link SimaAgent} calls the method broadcast of its {@link BasicBroadcast}.
 */
public class BroadcastWithCorrectAgentController implements Controller {

    // Constructors.

    public BroadcastWithCorrectAgentController(Map<String, String> ignored) {
    }

    // Methods.

    @Override
    public void execute() {
        try {
            List<Integer> correctAgents = getCorrectAgent();
            Collections.shuffle(correctAgents);
            SimaAgent chosenAgent = SimaSimulation.getAgent(correctAgents.get(0));
            BasicBroadcast basicBroadcast = (BasicBroadcast) chosenAgent.getProtocol(new ProtocolIdentifier(BasicBroadcast.class, "BasicBroadcast"));
            basicBroadcast.broadcast(
                    new StringMessage("HELLO WORLD", new ProtocolIdentifier(BasicBroadcastObserverProtocol.class, "ApplicationObserverProtocol")));
            SimaLog.info("The agent " + chosenAgent + " had broadcast the message");
        } catch (IOException e) {
            SimaLog.error("Fail to read correct agent", e);
        }
    }
}
