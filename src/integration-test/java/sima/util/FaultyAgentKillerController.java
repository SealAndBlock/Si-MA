package sima.util;

import sima.core.agent.SimaAgent;
import sima.core.scheduler.Controller;
import sima.core.simulation.SimaSimulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.util.DataManager.FAULTY_AGENT_PATH;

/**
 * Kill all {@link sima.core.agent.SimaAgent} which are in the file {@link  DataManager#FAULTY_AGENT_PATH}.
 */
public class FaultyAgentKillerController implements Controller {

    // Constructors.

    public FaultyAgentKillerController(Map<String, String> ignored) {
    }

    // Methods.

    @Override
    public void execute() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FAULTY_AGENT_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int agentId = Integer.parseInt(line);
                SimaAgent faultyAgent = SimaSimulation.getAgent(agentId);
                faultyAgent.kill();
            }
        } catch (IOException e) {
            SimaLog.error("Fail to read faulty agents", e);
        }
    }
}
