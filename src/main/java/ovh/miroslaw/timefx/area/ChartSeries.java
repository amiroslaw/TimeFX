package ovh.miroslaw.timefx.area;

import javafx.scene.chart.XYChart.Series;

import java.util.LongSummaryStatistics;

public record ChartSeries(String label, Series series, LongSummaryStatistics stats) {

}
