package ovh.miroslaw.timefx.pie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import ovh.miroslaw.timefx.model.TagDuration;
import ovh.miroslaw.timefx.model.TagType;
import ovh.miroslaw.timefx.model.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static ovh.miroslaw.timefx.pie.DateRangePie.ALL;

public class PieChartDataCreator {

    private final Map<String, List<Task>> cache = new HashMap<>();

    public PieChartDataCreator(List<Task> tasks) {
        this.cache.put(ALL.name(), Collections.unmodifiableList(tasks));
    }

    public ObservableList<Data> filterData(DateRangePie dateRange, TagType tagType) {
        final List<Task> filteredByDateRange = cache.computeIfAbsent(dateRange.name(), k ->
                cache.get(ALL.name()).parallelStream()
                        .filter(t -> t.getEnd() != null)
                        .filter(task -> dateRange.test(task.getStart()))
                        .toList()
        );
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
            final String label = String.format("%s (%.0f%%)  %d:%d", entry.getKey(), percent, Math.abs(tagSum.toHours()),
                    Math.abs(tagSum.toMinutesPart()));
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
