package ovh.miroslaw.timefx;

import ovh.miroslaw.timefx.area.DateRangeArea;
import ovh.miroslaw.timefx.model.Task;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

public class Boundary {

    private static final Integer LOWER_BOUNDARY = 1;
    private final DateRangeArea dateRange;
    private LocalDateTime startDate;
    private final Predicate<Task> isAfter = task -> task.getStart().isAfter(startDate.minusDays(1));
    private LocalDateTime endDate;
    private final Predicate<Task> isBefore = task -> task.getStart().isBefore(endDate.plusDays(1));
    public final Predicate<Task> isBetween = isAfter.and(isBefore);

    public Boundary(String dateRange) {
        this.dateRange = DateRangeArea.ofLowerCase(dateRange);
        setBoundaries();
    }

    public Boundary(DateRangeArea dateRange) {
        this.dateRange = dateRange;
        setBoundaries();
    }

    public Integer getLowerBoundary() {
        return LOWER_BOUNDARY;
    }

    public int getUpperBoundary() {
        return (int) Duration.between(this.startDate, this.endDate).toDays() + 1;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public DateRangeArea getDateRange() {
        return dateRange;
    }

    public String getDateRangeLabel() {
        return String.format("%s <=> %s", this.startDate.toLocalDate(), this.endDate.toLocalDate());
    }
    private void setBoundaries() {
        final LocalDateTime now = LocalDateTime.now();
        switch (dateRange) {
            case WEEK -> {
                startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = now;
            }
            case LAST_WEEK -> {
                startDate = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = now.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }
            case MONTH -> {
                this.startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                this.endDate = now;
            }
            case LAST_MONTH -> {
                startDate = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                endDate = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            }
            case DAYS_30 -> {
                this.startDate = now.minusMonths(1);
                this.endDate = now;
            }
            case QUARTER -> {
                this.startDate = now.with(now.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
                this.endDate = now;
            }
            case LAST_QUARTER -> {
                var firstDayOfQuarter = now.with(now.getMonth().firstMonthOfQuarter())
                        .with(TemporalAdjusters.firstDayOfMonth());
                startDate = firstDayOfQuarter.with(firstDayOfQuarter.minusDays(1).getMonth().firstMonthOfQuarter())
                        .with(TemporalAdjusters.firstDayOfMonth());
                endDate = firstDayOfQuarter.minusDays(1);
            }
            case SIX_MONTHS -> {
                this.startDate = now.minusMonths(6);
                this.endDate = now;
            }
            case YEAR -> {
                this.startDate = now.with(TemporalAdjusters.firstDayOfYear());
                this.endDate = now;
            }
            case LAST_YEAR -> {
                this.endDate = now.with(TemporalAdjusters.firstDayOfYear()).minusDays(1);
                this.startDate = endDate.with(TemporalAdjusters.firstDayOfYear());
            }
        }

    }

}
