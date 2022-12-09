package ovh.miroslaw.timefx;

import ovh.miroslaw.timefx.area.DateRangeArea;
import ovh.miroslaw.timefx.model.Task;
import ovh.miroslaw.timefx.pie.DateRangePie;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

public class Boundary {

    private static final Integer LOWER_BOUNDARY = 1;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final Predicate<Task> isAfter = task -> task.getStart().isAfter(startDate);
    private final Predicate<Task> isBefore = task -> task.getStart().isBefore(endDate.plusDays(1));
    private Predicate<Task> isInRange = task -> true;
    private String rangeName;

    public Boundary(String dateRange) {
        setBoundaries(dateRange.toUpperCase());
    }

    public Boundary(DateRangeArea range) {
        setBoundaries(range.name());
    }

    public Boundary(DateRangePie range) {
        setBoundaries(range.name());
    }

    public Predicate<Task> getIsInRange() {
        return isInRange;
    }

    public String getRangeName() {
        return rangeName;
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

    public String getDateRangeLabel() {
        final String start = this.startDate == null ? "-" : this.startDate.toLocalDate().toString();
        return String.format("%s <=> %s", start, this.endDate.toLocalDate());
    }

    private void setBoundaries(String range) {
        this.rangeName = range;
        final LocalDateTime now = LocalDateTime.now().with(LocalTime.MIDNIGHT);

        switch (range) {
            case "TODAY" -> {
                startDate = now;
                endDate = now;
                isInRange = t -> t.getStart().toLocalDate().equals(LocalDateTime.now().toLocalDate());
            }
            case "YESTERDAY" -> {
                startDate = now.minusDays(1);
                endDate = now;
                isInRange = t -> t.getStart().toLocalDate().equals(LocalDateTime.now().minusDays(1).toLocalDate());
            }
            case "WEEK" -> {
                startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "LAST_WEEK" -> {
                startDate = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = now.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                isInRange = isAfter.and(isBefore);
            }
            case "MONTH" -> {
                this.startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                this.endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "LAST_MONTH" -> {
                startDate = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                endDate = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                isInRange = isAfter.and(isBefore);
            }
            case "DAYS_30" -> {
                this.startDate = now.minusMonths(1);
                this.endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "QUARTER" -> {
                this.startDate = now.with(now.getMonth().firstMonthOfQuarter())
                        .with(TemporalAdjusters.firstDayOfMonth());
                this.endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "LAST_QUARTER" -> {
                var firstDayOfQuarter = now.with(now.getMonth().firstMonthOfQuarter())
                        .with(TemporalAdjusters.firstDayOfMonth());
                startDate = firstDayOfQuarter.with(firstDayOfQuarter.minusDays(1).getMonth().firstMonthOfQuarter())
                        .with(TemporalAdjusters.firstDayOfMonth());
                endDate = firstDayOfQuarter.minusDays(1);
                isInRange = isAfter.and(isBefore);
            }
            case "SIX_MONTHS" -> {
                this.startDate = now.minusMonths(6);
                this.endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "YEAR" -> {
                this.startDate = now.with(TemporalAdjusters.firstDayOfYear());
                this.endDate = now;
                isInRange = isAfter.and(isBefore);
            }
            case "LAST_YEAR" -> {
                this.endDate = now.with(TemporalAdjusters.firstDayOfYear()).minusDays(1);
                this.startDate = endDate.with(TemporalAdjusters.firstDayOfYear());
                isInRange = isAfter.and(isBefore);
            }
            default -> {
                this.endDate = now;
                isInRange = t -> true;
            }
        }
    }

}
