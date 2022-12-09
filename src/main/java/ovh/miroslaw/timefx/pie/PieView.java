package ovh.miroslaw.timefx.pie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import ovh.miroslaw.timefx.Boundary;
import ovh.miroslaw.timefx.View;
import ovh.miroslaw.timefx.model.TagType;

import java.util.List;

import static ovh.miroslaw.timefx.model.TagType.CONTEXTS;
import static ovh.miroslaw.timefx.pie.DateRangePie.WEEK;

public class PieView implements View {

    @Override
    public Pane getPane() {
        final PieChartDataCreator pieChartDataCreator = new PieChartDataCreator();

        ChoiceBox<String> tagTypes = TagType.createChoiceBox();
        ChoiceBox<String> dateRange = DateRangePie.createChoiceBox();

        final PieChart pieChart = new PieChart();
        pieChart.setData(pieChartDataCreator.filterData(new Boundary(WEEK), CONTEXTS));
        pieChart.setTitle(WEEK.name().toLowerCase() + ": " + getLabel(WEEK));
        pieChart.setTitle(getLabel(WEEK));
        pieChart.setPrefSize(1000, 650);

        tagTypes.setOnAction(e -> {
            final TagType type = TagType.ofLowerCase(((ChoiceBox<String>) e.getTarget()).getValue());
            final DateRangePie range = DateRangePie.ofLowerCase(dateRange.getValue());
            final ObservableList<Data> data = pieChartDataCreator.filterData(new Boundary(range), type);
            pieChart.setData(FXCollections.observableArrayList(data));
        });

        dateRange.setOnAction(e -> {
            final TagType type = TagType.ofLowerCase(tagTypes.getValue());
            final DateRangePie range = DateRangePie.ofLowerCase(((ChoiceBox<String>) e.getTarget()).getValue());
            final ObservableList<Data> data = pieChartDataCreator.filterData(new Boundary(range), type);
            pieChart.setTitle(getLabel(range));
            pieChart.setData(FXCollections.observableArrayList(data));
        });

        return this.createMainPane(pieChart, List.of(dateRange, tagTypes));
    }

    private String getLabel(DateRangePie range) {
        final String title = range.getLabel();
        try {
            return title + ": " + new Boundary(range.name()).getDateRangeLabel();
        } catch (IllegalArgumentException e) {
            return title;
        }
    }

}
