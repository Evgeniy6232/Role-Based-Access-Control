package com.evgenii.rbac.util;

import java.util.List;
import java.util.Objects;

public class FormatUtils {

    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || rows == null) return "";

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (var row : rows)
            for (int i = 0; i < row.length; i++)
                widths[i] = Math.max(widths[i], row[i].length());

        StringBuilder lineBuilder = new StringBuilder("+");
        for (int w : widths) lineBuilder.append("-".repeat(w + 2)).append("+");
        String line = lineBuilder.append("\n").toString();

        StringBuilder sb = new StringBuilder(line);

        for (int i = 0; i < headers.length; i++)
            sb.append("| ").append(headers[i]).append(" ".repeat(widths[i] - headers[i].length() + 1));
        sb.append("|\n").append(line);

        for (var row : rows) {
            for (int i = 0; i < row.length; i++) {
                String cell = row[i] + " ".repeat(widths[i] - row[i].length());
                sb.append("| ").append(cell).append(" ");
            }
            sb.append("|\n");
        }

        return sb.append(line).toString();
    }

    public static String formatHeader(String text) {
        return "=".repeat(text.length()) + text + "=".repeat(text.length());
    }

    public static String truncate(String text, int maxLength) {
        if (text == null) return " ";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String padRight(String text, int length){
        if (text == null) return " ";
        return text + " ".repeat(length - text.length());
    }

    public static String padLeft(String text, int length){
        if (text == null) return " ";
        return " ".repeat(length - text.length()) + text;
    }
}
