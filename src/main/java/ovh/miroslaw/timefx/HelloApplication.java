package ovh.miroslaw.timefx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, ParseException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Panel panel = new Panel("This is the title");

        final List<Task> tasks = getData();
        final List<Data> pieChartDataList = joinTagDuration(tasks);

        ObservableList<Data> pieChartData = FXCollections.observableArrayList(pieChartDataList);
        final PieChart chart = new PieChart(pieChartData);

        final VBox vBox = new VBox();
        vBox.getChildren().add(chart);
        vBox.setPadding(new Insets(20));

        Scene scene = new Scene(vBox);
        stage.setTitle("Context");
        stage.setWidth(500);
        stage.setHeight(500);
//        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();
    }

    private List<Data> joinTagDuration(List<Task> tasks) {
        List<PieChart.Data> summary = new ArrayList<>();
        final Map<String, List<Duration>> tagsMap = tasks.parallelStream()
                .map(this::mapTask)
                .collect(Collectors.groupingBy(Tag::name, mapping(Tag::duration, toList())));
        final long entireDuration = tagsMap.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Duration::toMinutes)
                .sum();
        for (Entry<String, List<Duration>> entry : tagsMap.entrySet()) {
            final Duration sum = entry.getValue()
                    .stream()
                    .reduce(Duration.ZERO, (s, e) -> e.plus(s));
            final float percent = (float) sum.toMinutes() / entireDuration * 100;
            final String label = String.format("%s (%.1f%%)", entry.getKey().substring(1), percent);
            summary.add(new PieChart.Data(label, sum.toMinutes()));
        }
        return summary;
    }

    private Tag mapTask(Task task) {
        final String context = task.getTags().stream()
                .filter(a -> a.startsWith("+"))
                .findAny()
                .orElse("empty");
        final Duration duration = Duration.between(task.getEnd(), task.getStart());
        return new Tag(context, duration);
    }

    private static List<Task> getData() {

        Process process = null;
        try {
            process = new ProcessBuilder("timew", "export", "yesterday").start();
            final int exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        final String output = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<List<Task>> taskRef = new TypeReference<>() {
        };

        List<Task> tasks = new ArrayList<>();
        try {
            tasks = mapper.readValue(output, taskRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return tasks;

    }

    public static void main(String[] args) {
        launch();
    }
}
