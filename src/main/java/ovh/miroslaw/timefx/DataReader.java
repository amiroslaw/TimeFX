package ovh.miroslaw.timefx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ovh.miroslaw.timefx.model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {

    private DataReader() {
    }

    public static List<Task> getData() {
        Process process;
        int exitCode;
        try {
            process = new ProcessBuilder("timew", "export").start();
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Could not find or run timewarrior");
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
        if (exitCode != 0) {
            return Collections.emptyList();
        }
        final String output = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<List<Task>> taskRef = new TypeReference<>() {
        };

        List<Task> tasks;
        try {
            tasks = mapper.readValue(output, taskRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return tasks;

    }
}
