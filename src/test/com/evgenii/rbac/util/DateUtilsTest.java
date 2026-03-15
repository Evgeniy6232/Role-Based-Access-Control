package com.evgenii.rbac.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {

    @Test
    void getCurrentDate_shouldReturnNonNull() {
        assertNotNull(DateUtils.getCurrentDate());
    }

    @Test
    void getCurrentDateTime_shouldReturnNonNull() {
        assertNotNull(DateUtils.getCurrentDateTime());
    }

    @Test
    void isBefore_withEarlierDate_shouldReturnTrue() {
        assertTrue(DateUtils.isBefore("2026-01-01", "2026-12-31"));
    }

    @Test
    void isBefore_withLaterDate_shouldReturnFalse() {
        assertFalse(DateUtils.isBefore("2026-12-31", "2026-01-01"));
    }

    @Test
    void isBefore_withEqualDates_shouldReturnFalse() {
        assertFalse(DateUtils.isBefore("2026-01-01", "2026-01-01"));
    }

    @Test
    void isBefore_withNull_shouldReturnFalse() {
        assertFalse(DateUtils.isBefore(null, "2026-01-01"));
        assertFalse(DateUtils.isBefore("2026-01-01", null));
    }

    @Test
    void isAfter_withLaterDate_shouldReturnTrue() {
        assertTrue(DateUtils.isAfter("2026-12-31", "2026-01-01"));
    }

    @Test
    void isAfter_withEarlierDate_shouldReturnFalse() {
        assertFalse(DateUtils.isAfter("2026-01-01", "2026-12-31"));
    }

    @Test
    void isAfter_withEqualDates_shouldReturnFalse() {
        assertFalse(DateUtils.isAfter("2026-01-01", "2026-01-01"));
    }

    @Test
    void isAfter_withNull_shouldReturnFalse() {
        assertFalse(DateUtils.isAfter(null, "2026-01-01"));
        assertFalse(DateUtils.isAfter("2026-01-01", null));
    }

    @Test
    void addDays_withPositiveDays_shouldAddDays() {
        assertEquals("2026-01-11", DateUtils.addDays("2026-01-01", 10));
        assertEquals("2026-02-01", DateUtils.addDays("2026-01-01", 31));
    }

    @Test
    void addDays_withNegativeDays_shouldSubtractDays() {
        assertEquals("2025-12-22", DateUtils.addDays("2026-01-01", -10));
    }

    @Test
    void addDays_withZeroDays_shouldReturnSame() {
        assertEquals("2026-01-01", DateUtils.addDays("2026-01-01", 0));
    }

    @Test
    void addDays_withNull_shouldReturnNull() {
        assertNull(DateUtils.addDays(null, 10));
    }

    @Test
    void formatRelativeTime_withFutureDate_shouldReturnInDays() {
        String result = DateUtils.formatRelativeTime("2030-01-01");
        assertTrue(result.startsWith("in "));
        assertTrue(result.contains("day"));
    }

    @Test
    void formatRelativeTime_withPastDate_shouldReturnAgo() {
        String result = DateUtils.formatRelativeTime("2020-01-01");
        assertTrue(result.contains("day") && result.contains("ago"));
    }

    @Test
    void formatRelativeTime_withToday_shouldReturnToday() {
        String today = DateUtils.getCurrentDate();
        assertEquals("today", DateUtils.formatRelativeTime(today));
    }

    @Test
    void formatRelativeTime_withNull_shouldReturnEmpty() {
        assertEquals("", DateUtils.formatRelativeTime(null));
    }
}