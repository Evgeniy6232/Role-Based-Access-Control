package com.evgenii.rbac.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class FormatUtilsTest {

    @Test
    void truncate_withShorterString_shouldReturnSame() {
        assertEquals("genna", FormatUtils.truncate("genna", 10));
    }

    @Test
    void truncate_withLongerString_shouldCutAndAddDots() {
        assertEquals("gennaqq...", FormatUtils.truncate("gennaqqqqqqqqqq", 10));
    }

    @Test
    void truncate_withNull_shouldReturnSpace() {
        assertEquals(" ", FormatUtils.truncate(null, 10));
    }

    @Test
    void truncate_withExactLength_shouldReturnSame() {
        assertEquals("1234567890", FormatUtils.truncate("1234567890", 10));
    }

    @Test
    void padRight_withShorterString_shouldAddSpaces() {
        assertEquals("genna     ", FormatUtils.padRight("genna", 10));
    }

    @Test
    void padRight_withExactLength_shouldReturnSame() {
        assertEquals("genna", FormatUtils.padRight("genna", 5));
    }

    @Test
    void padRight_withLongerString_shouldNotThrow() {
        assertDoesNotThrow(() -> FormatUtils.padRight("gennaqqqq", 5));
    }

    @Test
    void padRight_withNull_shouldReturnSpace() {
        assertEquals(" ", FormatUtils.padRight(null, 10));
    }

    @Test
    void padLeft_withShorterString_shouldAddSpaces() {
        assertEquals("     genna", FormatUtils.padLeft("genna", 10));
    }

    @Test
    void padLeft_withExactLength_shouldReturnSame() {
        assertEquals("genna", FormatUtils.padLeft("genna", 5));
    }

    @Test
    void padLeft_withLongerString_shouldNotThrow() {
        assertDoesNotThrow(() -> FormatUtils.padLeft("gennaqqqq", 5));
    }

    @Test
    void padLeft_withNull_shouldReturnSpace() {
        assertEquals(" ", FormatUtils.padLeft(null, 10));
    }

    @Test
    void formatHeader_shouldWrapWithEquals() {
        assertEquals("====test====", FormatUtils.formatHeader("test"));
        assertEquals("=========USER LIST=========", FormatUtils.formatHeader("USER LIST"));
    }

    @Test
    void formatTable_withNullHeaders_shouldReturnEmpty() {
        assertEquals("", FormatUtils.formatTable(null, List.of()));
    }

    @Test
    void formatTable_withNullRows_shouldReturnEmpty() {
        assertEquals("", FormatUtils.formatTable(new String[]{"h1"}, null));
    }

    @Test
    void formatTable_withEmptyData_shouldReturnTableWithHeaders() {
        String[] headers = {"User", "Role"};
        List<String[]> rows = List.of();
        String result = FormatUtils.formatTable(headers, rows);
        assertTrue(result.contains("User"));
        assertTrue(result.contains("Role"));
        assertTrue(result.contains("+"));
    }

    @Test
    void formatTable_withData_shouldReturnFormattedTable() {
        String[] headers = {"User", "Role"};
        List<String[]> rows = List.of(
                new String[]{"genna", "ADMIN"},
                new String[]{"misha", "USER"}
        );
        String result = FormatUtils.formatTable(headers, rows);
        assertTrue(result.contains("genna"));
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("misha"));
        assertTrue(result.contains("USER"));
        assertTrue(result.contains("+"));
    }
}