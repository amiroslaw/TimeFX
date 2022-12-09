package ovh.miroslaw.timefx.area;

import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;
import ovh.miroslaw.timefx.Boundary;
import ovh.miroslaw.timefx.TaskCache;
import ovh.miroslaw.timefx.model.TagTask;
import ovh.miroslaw.timefx.model.TagType;
import ovh.miroslaw.timefx.model.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summarizingLong;
import static java.util.stream.Collectors.toList;

public class AreaChartDataCreator {

    public static final String ALL = "All";

    public List<ChartSeries> filterData(Boundary boundary, TagType tagType) {
        final List<Task> filteredByDateRange = TaskCache.getTasks(boundary);

        final Map<LocalDate, List<Duration>> groupedByDay = groupedByDay(filteredByDateRange, boundary);
        final List<ChartSeries> chartSeries = new ArrayList<>();
        chartSeries.add(buildDataSeries(groupedByDay, boundary, ALL));
        if (tagType != TagType.TASKS) {
            chartSeries.addAll(groupByContext(filteredByDateRange, boundary, tagType));
        }
        return chartSeries;
    }

    private List<ChartSeries> groupByContext(List<Task> tasks, Boundary boundary, TagType type) {
        final Map<String, List<Task>> contexts = groupByTagType(tasks, type);
        return contexts.entrySet().stream()
                .map(e -> buildDataSeries(groupedByDay(e.getValue(), boundary), boundary, e.getKey()))
                .toList();
    }

    private Map<String, List<Task>> groupByTagType(List<Task> tasks, TagType type) {
        return tasks.parallelStream()
                .map(task -> task.mapTagName(type))
                .flatMap(Optional::stream)
                .collect(groupingBy(TagTask::name, mapping(TagTask::task, toList())));
    }

    private ChartSeries buildDataSeries(Map<LocalDate, List<Duration>> tasks, Boundary boundary, String label) {
        XYChart.Series series = new XYChart.Series<>();
        series.setName(label);

        List<XYChart.Data> data = new ArrayList<>();

        int xAxisValue = 1;
        for (LocalDate date = boundary.getStartDate().toLocalDate();
                date.isBefore(boundary.getEndDate().plusDays(1).toLocalDate()); date = date.plusDays(1)) {
            final long sumOfDay = reduceDuration(tasks, date);
            data.add(new XYChart.Data(xAxisValue, sumOfDay));
            xAxisValue++;
        }
        final LongSummaryStatistics stats = data.parallelStream()
                .collect(summarizingLong(t -> (long) t.getYValue()));
        series.setData(FXCollections.observableArrayList(data));
        return new ChartSeries(label, series, stats);
    }

    private Map<LocalDate, List<Duration>> groupedByDay(List<Task> tasks, Boundary boundary) {
        final Function<Task, Duration> sumDuration = t -> Duration.between(t.getStart(), t.getEnd());
        return tasks.parallelStream()
                .filter(boundary.getIsInRange())
                .collect(groupingBy(task -> task.getStart().toLocalDate(),
                        mapping(sumDuration, toList())
                ));
    }

    private long reduceDuration(Map<LocalDate, List<Duration>> groupedByDay, LocalDate day) {
        if (groupedByDay.get(day) == null) {
            return 0;
        }
        return groupedByDay.get(day)
                .stream()
                .reduce(Duration.ZERO, (s, e) -> e.plus(s))
                .toMinutes();
    }

}
