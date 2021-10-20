package sima.core.simulation.configuration.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.exception.ConfigurationException;
import sima.core.exception.FailInstantiationException;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.SimaSimulation;
import sima.core.simulation.SimulationSetup;
import sima.core.simulation.configuration.json.ArgumentativeObjectJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConfigurationParser {

    // Variables.

    private final String configurationJsonPath;

    private final ConfigurationBundle configurationBundle;

    // Constructors.

    public ConfigurationParser(String configurationJsonPath) {
        this.configurationJsonPath = configurationJsonPath;
        configurationBundle = new ConfigurationBundle();
    }

    // Methods.

    public ConfigurationBundle parseSimulation()
            throws ConfigurationException, FailInstantiationException, IOException, ClassNotFoundException, NoSuchMethodException {
        fillBundle();
        return configurationBundle;
    }

    private void fillBundle()
            throws IOException, ConfigurationException, FailInstantiationException, ClassNotFoundException, NoSuchMethodException {
        SimaSimulationJson simaSimulationJson = getSimaSimulationJsonFromFile(configurationJsonPath);
        AgentParser agentParser = new AgentParser(simaSimulationJson);
        SchedulerParser schedulerParser = new SchedulerParser(simaSimulationJson);
        ControllerParser controllerParser = new ControllerParser(simaSimulationJson);
        SimulationSetupParser simulationSetupParser = new SimulationSetupParser(simaSimulationJson);
        SimaWatcherParser simaWatcherParser = new SimaWatcherParser(simaSimulationJson);

        agentParser.parseAgents();
        schedulerParser.parseScheduler();
        controllerParser.parseControllers();
        simulationSetupParser.parseSimulationSetup();
        simaWatcherParser.parseSimaWatcher();

        controllerParser.scheduleControllers(controllerParser.getControllers(), schedulerParser.getScheduler());

        fillBundle(schedulerParser.getScheduler(), agentParser.getAllAgents(), agentParser.getAllEnvironments(),
                   simulationSetupParser.getSimulationSetup(),
                   simaWatcherParser.getSimaWatcher());
    }

    private void fillBundle(Scheduler scheduler, Set<SimaAgent> allAgents, Set<Environment> allEnvironments, SimulationSetup simulationSetupClass,
                            SimaSimulation.SimaWatcher simaWatcher) {
        configurationBundle.setScheduler(scheduler);
        configurationBundle.setAllAgents(allAgents);
        configurationBundle.setAllEnvironments(allEnvironments);
        configurationBundle.setSimulationSetup(simulationSetupClass);
        configurationBundle.setSimaWatcher(simaWatcher);
    }

    // SimaSimulationJson.

    private @NotNull SimaSimulationJson getSimaSimulationJsonFromFile(String configurationJsonPath) throws IOException {
        final var gson = createGson();
        return getSimaSimulationJsonFromFile(configurationJsonPath, gson);
    }

    private @NotNull Gson createGson() {
        final var builder = new GsonBuilder();
        return builder.create();
    }

    private @NotNull SimaSimulationJson getSimaSimulationJsonFromFile(String filePath, Gson gson)
            throws IOException, JsonSyntaxException {
        try (var reader = new JsonReader(new FileReader(filePath))) {
            return gson.fromJson(reader, SimaSimulationJson.class);
        }
    }

    // Static.

    public static Map<String, String> parseArgs(ArgumentativeObjectJson argumentativeObjectJson)
            throws ConfigurationException {

        if (argumentativeObjectJson.getArgs() != null) {
            Map<String, String> args = new HashMap<>();
            for (List<String> argsCouple : argumentativeObjectJson.getArgs())
                if (argsCouple.size() == 2)
                    args.put(Optional.of(argsCouple.get(0)).get(), argsCouple.get(1));
                else
                    throw new ConfigurationException(
                            "Wrong format for argument. In Json Sima config, an args is an array of only 2 values: the args name and its value" +
                                    ". Ex: [ \"argName\", \"argValue\"]");
            return args;
        } else
            return Collections.emptyMap();
    }

    public static @NotNull String extractSetterName(String fieldName) {
        char[] attributeNameCharArray = fieldName.toCharArray();
        attributeNameCharArray[0] = fieldName.toUpperCase().charAt(0);
        var formattedFieldName = new String(attributeNameCharArray);
        return "set" + formattedFieldName;
    }

    // Inner classes.

    /**
     * All elements that the {@link ConfigurationParser} will returns after have parsed the configuration.
     * <p>
     * It contains all elements that a {@link SimaSimulation} need to be created and started.
     */
    public static class ConfigurationBundle {

        // Variables.

        private Scheduler scheduler;

        private Set<SimaAgent> allAgents;

        private Set<Environment> allEnvironments;

        private SimulationSetup simulationSetup;

        private SimaSimulation.SimaWatcher simaWatcher;

        // Getters and Setters.

        public Scheduler getScheduler() {
            return scheduler;
        }

        public void setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        public Set<SimaAgent> getAllAgents() {
            return allAgents;
        }

        public void setAllAgents(Set<SimaAgent> allAgents) {
            this.allAgents = allAgents;
        }

        public Set<Environment> getAllEnvironments() {
            return allEnvironments;
        }

        public void setAllEnvironments(Set<Environment> allEnvironments) {
            this.allEnvironments = allEnvironments;
        }

        public SimulationSetup getSimulationSetup() {
            return simulationSetup;
        }

        public void setSimulationSetup(SimulationSetup simulationSetup) {
            this.simulationSetup = simulationSetup;
        }

        public SimaSimulation.SimaWatcher getSimaWatcher() {
            return simaWatcher;
        }

        public void setSimaWatcher(SimaSimulation.SimaWatcher simaWatcher) {
            this.simaWatcher = simaWatcher;
        }
    }
}
