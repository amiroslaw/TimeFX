package ovh.miroslaw.timefx.area;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ovh.miroslaw.timefx.EnumHelper;


public enum DateRangeArea {

    WEEK("Week"),
    LAST_WEEK("Last week"),
    MONTH("Current month"),
    LAST_MONTH("Last month"),
    DAYS_30("30 last days"),
    QUARTER("Current quarter"),
    LAST_QUARTER("Last quarter"),
    SIX_MONTHS("6 months"),
    YEAR("Year"),
    LAST_YEAR("Last year");

    public static ChoiceBox<String> createChoiceBox() {
        final String[] range = EnumHelper.getNames(DateRangeArea.class);
        ChoiceBox<String> dateRange = new ChoiceBox<>(FXCollections.observableArrayList(range));
        dateRange.setValue(EnumHelper.getLowerCase(DAYS_30));
        return dateRange;
    }

    private final String label;

    DateRangeArea(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DateRangeArea ofLowerCase(String lowerCase) {
        return DateRangeArea.valueOf(lowerCase.toUpperCase());
    }
}
