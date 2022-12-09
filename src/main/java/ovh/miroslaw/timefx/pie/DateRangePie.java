package ovh.miroslaw.timefx.pie;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ovh.miroslaw.timefx.EnumHelper;

public enum DateRangePie {

    TODAY("today"),
    YESTERDAY("yesterday"),
    WEEK("Week"),
    LAST_WEEK("Last week"),
    MONTH("Current month"),
    LAST_MONTH("Last month"),
    DAYS_30("Last 30 days"),
    QUARTER("Current quarter"),
    LAST_QUARTER("Last quarter"),
    SIX_MONTHS("Last 6 months"),
    YEAR("Year"),
    LAST_YEAR("Last year"),
    ALL("All tasks");

    public static ChoiceBox<String> createChoiceBox() {
        final String[] range = EnumHelper.getNames(DateRangePie.class);
        ChoiceBox<String> dateRange = new ChoiceBox<>(FXCollections.observableArrayList(range));
        dateRange.setValue(EnumHelper.getLowerCase(MONTH));
        return dateRange;
    }

    private final String label;

    DateRangePie(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DateRangePie ofLowerCase(String lowerCase) {
        return DateRangePie.valueOf(lowerCase.toUpperCase());
    }
}
