package ovh.miroslaw.timefx.pie;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ovh.miroslaw.timefx.Boundary;
import ovh.miroslaw.timefx.EnumHelper;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public enum DateRangePie {

    TODAY(d -> equals(d, LocalDateTime.now())),
    YESTERDAY(d -> equals(d, LocalDateTime.now().minusDays(1))),
    WEEK(d -> isInRange(d, "WEEK")),
    LAST_WEEK(d -> isInRange(d, "LAST_WEEK")),
    MONTH(d -> isInRange(d, "MONTH")),
    LAST_MONTH(d -> isInRange(d, "LAST_MONTH")),
    DAYS_30(d -> isInRange(d, "DAYS_30")),
    QUARTER(d -> isInRange(d, "QUARTER")),
    LAST_QUARTER(d -> isInRange(d, "LAST_QUARTER")),
    SIX_MONTH(d -> isInRange(d, "SIX_MONTHS")),
    YEAR(d -> isInRange(d, "YEAR")),
    LAST_YEAR(d -> isInRange(d, "LAST_YEAR")),
    ALL(d -> true);

    private static boolean isInRange(LocalDateTime date, String range) {
        final Boundary boundary = new Boundary(range);
        return date.isAfter(boundary.getStartDate().minusDays(1)) && date.isBefore(boundary.getEndDate().plusDays(1));
    }

    private static boolean equals(LocalDateTime actual, LocalDateTime expected) {
        return actual.toLocalDate().equals(expected.toLocalDate());
    }

    public static ChoiceBox<String> createChoiceBox() {
        final String[] range = EnumHelper.getNames(DateRangePie.class);
        ChoiceBox<String> dateRange = new ChoiceBox<>(FXCollections.observableArrayList(range));
        dateRange.setValue(EnumHelper.getLowerCase(MONTH));
        return dateRange;
    }

    private final Predicate<LocalDateTime> predicate;

    DateRangePie(Predicate<LocalDateTime> predicate) {
        this.predicate = predicate;
    }

    public boolean test(LocalDateTime datetime) {
        return predicate.test(datetime);
    }

    public static DateRangePie ofLowerCase(String lowerCase) {
        return DateRangePie.valueOf(lowerCase.toUpperCase());
    }
}
