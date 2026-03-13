package com.evgenii.rbac.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }

    public static boolean isBefore(String date1, String date2) {
        if (date1 == null || date2 == null) return false;
        return date1.compareTo(date2) < 0;
    }

    public static boolean isAfter(String date1, String date2) {
        if (date1 == null || date2 == null) return false;
        return date1.compareTo(date2) > 0;
    }

    public static String addDays(String date, int days) {
        if (date == null) return null;
        return LocalDate.parse(date).plusDays(days).toString();
    }

    public static String formatRelativeTime(String date) {
        if (date == null) return "";

        LocalDate target = LocalDate.parse(date);
        LocalDate now = LocalDate.now();

        long days = java.time.temporal.ChronoUnit.DAYS.between(now, target);

        if (days == 0) return "today";
        if (days > 0) return "in " + days + " day" + (days > 1 ? "s" : "");
        return (-days) + " day" + (-days > 1 ? "s" : "") + " ago";
    }
}