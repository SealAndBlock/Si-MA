package sima.util;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.SimaAgent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sima.core.simulation.SimaSimulation.SimaLog;


public class DataManager {

    // Constants.

    public static String DATA_PREFIX = "src/integration-test/resources/data/";
    public static String AGENT_DELIVER_MESSAGE_PATH = DATA_PREFIX + "agent_which_deliver.txt";
    public static String AGENT_NB_DELIVERY_PATH = DATA_PREFIX + "agent_nb_delivery.txt";
    public static final String CORRECT_AGENT_PATH = DATA_PREFIX + "correct_agent.txt";
    public static final String FAULTY_AGENT_PATH = DATA_PREFIX + "faulty_agent.txt";

    // Methods.

    public static void clearDataFile() {
        deleteIfExists(AGENT_DELIVER_MESSAGE_PATH);
        deleteIfExists(AGENT_NB_DELIVERY_PATH);
        deleteIfExists(CORRECT_AGENT_PATH);
        deleteIfExists(FAULTY_AGENT_PATH);
    }

    public static void deleteIfExists(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            SimaLog.error("Fail to delete the file " + filePath, e);
        }
    }

    // Inner classes.

    public static class DataWriter {

        public static void writeLine(String filePath, String line) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(line);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                SimaLog.error("Fail to write line", e);
            }
        }

        public static void writeAgentId(String filePath, SimaAgent simaAgent) {
            try (Writer resWriter = new BufferedWriter(new FileWriter(filePath, true))) {
                resWriter.write(simaAgent.getUniqueId() + "\n");
                resWriter.flush();
            } catch (IOException e) {
                SimaLog.error("Error during write id", e);
            }
        }

    }

    public static class DataReader {

        // Methods.

        @NotNull
        public static List<String> getFileLines(String filePath) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            }
        }

        @NotNull
        public static List<Integer> getCorrectAgent() throws IOException {
            return getAgentFromFile(CORRECT_AGENT_PATH);
        }

        @NotNull
        public static List<Integer> getFaultyAgent() throws IOException {
            return getAgentFromFile(CORRECT_AGENT_PATH);
        }

        @NotNull
        public static List<Integer> getAgentFromFile(String filePath) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String agentId;
                List<Integer> agents = new ArrayList<>();
                while ((agentId = reader.readLine()) != null) {
                    int id = Integer.parseInt(agentId);
                    agents.add(id);
                }

                return agents;
            }
        }

    }

}
