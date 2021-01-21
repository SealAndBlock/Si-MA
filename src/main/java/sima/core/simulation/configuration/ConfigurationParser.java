package sima.core.simulation.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;

public class ConfigurationParser {

    // Static methods.

    /**
     * Parse the json file enter in parameter to create a {@link SimaSimulationJson}. This instance contain all
     * information of the configuration.
     *
     * @param configurationJsonPath the path of the json file
     * @return a instance of {@link SimaSimulationJson} if the configuration parsing success.
     * @throws IOException         for relative problems to the file path
     * @throws JsonSyntaxException if relative problems to the json syntax
     */
    public static @NotNull SimaSimulationJson parseConfiguration(String configurationJsonPath)
            throws IOException, JsonSyntaxException {
        final Gson gson = createGson();
        return getSimSimulationJSONFromFile(configurationJsonPath, gson);
    }

    private @NotNull
    static SimaSimulationJson getSimSimulationJSONFromFile(String filePath, Gson gson)
            throws IOException, JsonSyntaxException {
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            return gson.fromJson(reader, SimaSimulationJson.class);
        }
    }

    private @NotNull
    static Gson createGson() {
        final GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }
}
