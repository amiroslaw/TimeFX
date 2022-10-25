package ovh.miroslaw.timefx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundaryTest {

    private LocalDateTime wednesday;

    @BeforeEach
    void setUp() {
        this.wednesday = LocalDateTime.of(2022, 10, 26, 11, 11);
    }

    @Test
    void shouldCreateBoundaries_forCurrentMonth() {
        var expectedStart = LocalDateTime.of(2022, 10, 1, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 10, 26, 11, 11);

        var startDate = wednesday.with(TemporalAdjusters.firstDayOfMonth());
        var endDate = wednesday;
        var upperBoundary = Duration.between(startDate, endDate).toDays() + 1;

        assertEquals(26, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forLastMonth() {
        var expectedStart = LocalDateTime.of(2022, 9, 1, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 9, 30, 11, 11);

        var startDate = wednesday.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        var endDate = wednesday.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        var upperBoundary = endDate.getDayOfMonth();

        assertEquals(30, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forLastThreeMonth() {
        var expectedStart = LocalDateTime.of(2022, 7, 26, 11, 11);
        var expectedEnd = wednesday;

        var startDate = wednesday.minusMonths(3);
        var endDate = wednesday;
        var upperBoundary = Duration.between(startDate, endDate).toDays();

        assertEquals(92, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forCurrentWeek() {
        var expectedStart = LocalDateTime.of(2022, 10, 24, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 10, 26, 11, 11);

        var startDate = wednesday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        var endDate = wednesday;
        var upperBoundary = Duration.between(startDate, endDate).toDays() + 1;

        assertEquals(3, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forLastWeek() {
        var expectedStart = LocalDateTime.of(2022, 10, 17, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 10, 23, 11, 11);

        var startDate = wednesday.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        var endDate = wednesday.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        var upperBoundary = Duration.between(startDate, endDate).toDays() + 1;

        assertEquals(7, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forQuarter() {
        var expectedStart = LocalDateTime.of(2022, 10, 1, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 10, 26, 11, 11);

        var startDate = wednesday.with(wednesday.getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
        var endDate = wednesday;
        var upperBoundary = Duration.between(startDate, endDate).toDays() + 1;

        assertEquals(26, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldCreateBoundaries_forLastQuarter() {
        var expectedStart = LocalDateTime.of(2022, 7, 1, 11, 11);
        var expectedEnd = LocalDateTime.of(2022, 9, 30, 11, 11);

        var firstDayOfQuarter = wednesday.with(wednesday.getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
        var startDate = firstDayOfQuarter.with(firstDayOfQuarter.minusDays(1).getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
        var endDate = firstDayOfQuarter.minusDays(1);
        var upperBoundary = Duration.between(startDate, endDate).toDays() + 1;

        assertEquals(92, upperBoundary);
        assertEquals(expectedStart, startDate);
        assertEquals(expectedEnd, endDate);
    }

    @Test
    void shouldDateBeBeforeBoundary() {
        final Predicate<LocalDateTime> isBefore = t -> t.isBefore(wednesday.plusDays(1));
        assertTrue(isBefore.test(wednesday));
    }

    @Test
    void shouldDateBeAfterBoundary() {
        final Predicate<LocalDateTime> isAfter = t -> t.isAfter(wednesday.minusDays(1));
        assertTrue(isAfter.test(wednesday));
    }
}
