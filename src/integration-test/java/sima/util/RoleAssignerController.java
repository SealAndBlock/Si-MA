package sima.util;

import sima.core.agent.AgentIdentifier;
import sima.core.scheduler.Controller;
import sima.core.simulation.SimaSimulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.standard.broadcast.basic.BasicBroadcastIT.ENVIRONMENT_NAME;
import static sima.standard.broadcast.basic.BasicBroadcastIT.NB_AGENT;
import static sima.util.DataManager.CORRECT_AGENT_PATH;
import static sima.util.DataManager.FAULTY_AGENT_PATH;

/**
 * Decide if a {@link sima.core.agent.SimaAgent} will be faulty or correct.
 */
public class RoleAssignerController implements Controller {

    // Static.

    public static final String NB_FAULTY_AGENT_ARG = "nbFaultyAgent";

    private int nbFaultyAgent = 0;

    // Constructors.

    public RoleAssignerController(Map<String, String> args) {
        parseArgument(args);
    }

    // Methods.

    protected void parseArgument(Map<String, String> args) {
        parseNbFaultyAgent(args);
    }

    private void parseNbFaultyAgent(Map<String, String> args) {
        if (args.containsKey(NB_FAULTY_AGENT_ARG)) {
            try {
                nbFaultyAgent = Integer.parseInt(args.get(NB_FAULTY_AGENT_ARG));
                verifyNbFaultyAgentArg();
            } catch (NumberFormatException e) {
                SimaLog.error("Fail to parse Int from the arg " + NB_FAULTY_AGENT_ARG + " because it is not a number. Is current value is " +
                                      args.get(NB_FAULTY_AGENT_ARG), e);
            }
        } else
            nbFaultyAgent = 0;
    }

    private void verifyNbFaultyAgentArg() {
        if (nbFaultyAgent < 0)
            nbFaultyAgent = 0;
        if (nbFaultyAgent >= NB_AGENT)
            nbFaultyAgent = NB_AGENT - 1;
    }

    @Override
    public void execute() {
        List<AgentIdentifier> allAgents = new ArrayList<>(SimaSimulation.getEnvironment(ENVIRONMENT_NAME).getEvolvingAgentIdentifiers());
        Collections.shuffle(allAgents);
        List<AgentIdentifier> faultyAgents = allAgents.subList(0, nbFaultyAgent);
        List<AgentIdentifier> correctAgents = allAgents.subList(nbFaultyAgent, allAgents.size());

        writeRoleInfo(faultyAgents, correctAgents);
    }

    private void writeRoleInfo(List<AgentIdentifier> faultyAgents, List<AgentIdentifier> correctAgents) {
        try (Writer writerFaulty = new BufferedWriter(new FileWriter(FAULTY_AGENT_PATH)); Writer writerCorrect =
                new BufferedWriter(new FileWriter(CORRECT_AGENT_PATH))) {
            writeFaultyAgent(faultyAgents, writerFaulty);
            writeCorrectAgent(correctAgents, writerCorrect);
        } catch (IOException e) {
            SimaLog.error("Fail to write agents roles", e);
        }
    }

    private void writeCorrectAgent(List<AgentIdentifier> correctAgents, Writer writerCorrect) {
        correctAgents.forEach(correct -> {
            try {
                writerCorrect.write(correct.getAgentUniqueId() + "\n");
                writerCorrect.flush();
            } catch (IOException e) {
                SimaLog.error("Fail to write correct agent " + correct, e);
            }
        });
    }

    private void writeFaultyAgent(List<AgentIdentifier> faultyAgents, Writer writerFaulty) {
        faultyAgents.forEach(faulty -> {
            try {
                writerFaulty.write(faulty.getAgentUniqueId() + "\n");
                writerFaulty.flush();
            } catch (IOException e) {
                SimaLog.error("Fail to write faulty agent " + faulty, e);
            }
        });
    }
}
