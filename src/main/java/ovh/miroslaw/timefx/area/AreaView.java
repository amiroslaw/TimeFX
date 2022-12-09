package ovh.miroslaw.timefx.area;

import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ovh.miroslaw.timefx.Boundary;
import ovh.miroslaw.timefx.View;
import ovh.miroslaw.timefx.model.TagType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.LongFunction;

import static ovh.miroslaw.timefx.area.DateRangeArea.DAYS_30;
import static ovh.miroslaw.timefx.model.TagType.CONTEXTS;

public class AreaView implements View {

    private final HBox statPane = new HBox();
    private AreaChart<Number, Number> areaChart;
    private final Label caption = new Label("");

    @Override
    public Pane getPane() {
        final AreaChartDataCreator areaChartDataCreator = new AreaChartDataCreator();

        ChoiceBox<String> dateRange = DateRangeArea.createChoiceBox();
        ChoiceBox<String> tagTypes = TagType.createChoiceBox();

        Boundary boundary = new Boundary(DAYS_30);
        final List<ChartSeries> series = areaChartDataCreator.filterData(boundary, CONTEXTS);
        initChart(boundary, series);
        addListenerOnSeries(areaChart);
        statPane.getChildren().addAll(buildStatsPanel(series));
        statPane.setSpacing(50);
        statPane.setAlignment(Pos.CENTER);
        caption.setStyle("-fx-font: 20 arial;");
        final VBox mainPane = this.createMainPane(areaChart, List.of(dateRange, tagTypes));
        mainPane.getChildren().addAll(caption, statPane);

        dateRange.setOnAction(e -> {
            final String range = ((ChoiceBox<String>) e.getTarget()).getValue();
            fillUIControls(areaChartDataCreator, range, tagTypes.getValue());
            refreshChart(areaChart, range);
        });

        tagTypes.setOnAction(e -> {
            final String type = ((ChoiceBox<String>) e.getTarget()).getValue();
            fillUIControls(areaChartDataCreator, dateRange.getValue(), type);
            refreshChart(areaChart, dateRange.getValue());
        });

        return mainPane;
    }

    private void initChart(Boundary boundary, List<ChartSeries> series) {
        final NumberAxis xAxis = new NumberAxis(boundary.getLowerBoundary(), boundary.getUpperBoundary(), 1);
        final NumberAxis yAxis = new NumberAxis();
        areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setTitle(DAYS_30.getLabel() + ": " + boundary.getDateRangeLabel());
        areaChart.setAnimated(false);
        areaChart.setMinSize(1200, 400);
        series.forEach(s -> areaChart.getData().add(s.series()));
    }

    private void fillUIControls(AreaChartDataCreator dataCreator, String range, String tagType) {
        final TagType type = TagType.ofLowerCase(tagType);
        final Boundary boundary = new Boundary(range);
        final List<ChartSeries> chartSeries = dataCreator.filterData(boundary, type);
        areaChart.getData().clear();
        chartSeries.forEach(s -> areaChart.getData().add(s.series()));
        statPane.getChildren().setAll(buildStatsPanel(chartSeries));
    }

    private void refreshChart(AreaChart<Number, Number> areaChart, String range) {
        final DateRangeArea dateRangeArea = DateRangeArea.ofLowerCase(range);
        final Boundary boundary = new Boundary(dateRangeArea);

        NumberAxis xAxis = (NumberAxis) areaChart.getXAxis();
        areaChart.setTitle(dateRangeArea.getLabel() + ": " + boundary.getDateRangeLabel());
        xAxis.setLowerBound(boundary.getLowerBoundary());
        xAxis.setUpperBound(boundary.getUpperBoundary());
        xAxis.setTickUnit(1);

        addListenerOnSeries(areaChart);
    }

    private void addListenerOnSeries(AreaChart<Number, Number> areaChart) {
        areaChart.getData().forEach(
                s -> s.getNode().setOnMouseEntered((MouseEvent e) -> caption.setText(s.getName())));
    }

    private List<Label> buildStatsPanel(List<ChartSeries> chartSeries) {
        LongFunction<String> toTime = l -> {
            final Duration duration = Duration.ofMinutes(l);
            final int minutes = Math.abs(duration.toMinutesPart());
            final String minutesString = minutes <= 9 ? "0" + minutes : String.valueOf(minutes);
            return String.format("%d:%s", Math.abs(duration.toHours()), minutesString);
        };
        final List<Label> labels = new ArrayList<>();
        String description = """
                     Stats for
                     summary:
                     average:
                     max:
                """;
        labels.add(new Label(description));

        for (ChartSeries series : chartSeries) {
            final LongSummaryStatistics stats = series.stats();
            String txt = String.format("%s:%n%s%n%s%n%s%n", series.label(),
                    toTime.apply(stats.getSum()),
                    toTime.apply(Math.round(stats.getAverage())),
                    toTime.apply(stats.getMax()));
            labels.add(new Label(txt));
        }
        return labels;
    }
}
