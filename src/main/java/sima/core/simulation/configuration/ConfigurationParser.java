package sima.core.simulation.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.io.FileReader;
import java.io.IOException;

public class ConfigurationParser {

    // Constructors.

    private ConfigurationParser() {
    }

    // Static methods.

    /**
     * Parse the json file enter in parameter to create a {@link SimaSimulationJson}. This instance contains all information of the configuration.
     *
     * @param configurationJsonPath the path of the json file
     *
     * @return an instance of {@link SimaSimulationJson} if the configuration parsing success.
     *
     * @throws IOException         for relative problems to the file path
     * @throws JsonSyntaxException if relative problems to the json syntax
     */
    public static @NotNull SimaSimulationJson parseConfiguration(String configurationJsonPath)
            throws IOException, JsonSyntaxException {
        final var gson = createGson();
        return getSimaSimulationJSONFromFile(configurationJsonPath, gson);
    }

    private static @NotNull SimaSimulationJson getSimaSimulationJSONFromFile(String filePath, Gson gson)
            throws IOException, JsonSyntaxException {
        try (var reader = new JsonReader(new FileReader(filePath))) {
            return gson.fromJson(reader, SimaSimulationJson.class);
        }
    }

    private static @NotNull Gson createGson() {
        final var builder = new GsonBuilder();
        return builder.create();
    }
}
