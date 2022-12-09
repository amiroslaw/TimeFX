package ovh.miroslaw.timefx.pie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import ovh.miroslaw.timefx.Boundary;
import ovh.miroslaw.timefx.TaskCache;
import ovh.miroslaw.timefx.model.TagDuration;
import ovh.miroslaw.timefx.model.TagType;
import ovh.miroslaw.timefx.model.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

public class PieChartDataCreator {

    public ObservableList<Data> filterData(Boundary dateRange, TagType tagType) {
        final List<Task> filteredByDateRange = TaskCache.getTasks(dateRange);
        final Map<String, List<Duration>> tagsMap = groupByTagType(filteredByDateRange, tagType);
        final List<Data> pieChartDataList = buildChartData(tagsMap);
        return FXCollections.observableArrayList(pieChartDataList);
    }

    private List<Data> buildChartData(Map<String, List<Duration>> tasks) {
        List<PieChart.Data> summary = new ArrayList<>();

        final long entireDuration = sumTasksDuration(tasks);

        for (Entry<String, List<Duration>> entry : tasks.entrySet()) {
            final Duration tagSum = reduceDuration(entry);
            final float percent = (float) tagSum.toMinutes() / entireDuration * 100;
            final int minutes = Math.abs(tagSum.toMinutesPart());
            final String minutesString = minutes <= 9 ? "0" + minutes : String.valueOf(minutes);
            final String label = String.format("%s (%.0f%%)  %d:%s", entry.getKey(), percent,
                    Math.abs(tagSum.toHours()),
                    minutesString);
            summary.add(new PieChart.Data(label, tagSum.toMinutes()));
        }
        return summary.parallelStream()
                .sorted(Comparator.comparingDouble(PieChart.Data::getPieValue))
                .toList();
    }

    private long sumTasksDuration(Map<String, List<Duration>> tasks) {
        return tasks.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Duration::toMinutes)
                .sum();
    }

    private Duration reduceDuration(Entry<String, List<Duration>> entry) {
        return entry.getValue()
                .stream()
                .reduce(Duration.ZERO, (s, e) -> e.plus(s));
    }

    private Map<String, List<Duration>> groupByTagType(List<Task> tasks, TagType type) {
        return tasks.parallelStream()
                .map(task -> task.mapToTagDuration(type))
                .collect(groupingBy(TagDuration::name, mapping(TagDuration::duration, Collectors.toList())));
    }

}
