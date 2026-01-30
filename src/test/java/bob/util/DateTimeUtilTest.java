package bob.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link DateTimeUtil}.
 */
@DisplayName("DateTimeUtil")
class DateTimeUtilTest {

    // ---------- parseUserDateTime ----------

    @Test
    @DisplayName("parseUserDateTime: yyyy-MM-dd HHmm format")
    void parseUserDateTime_isoDateTime_parses() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2025-01-15 1800");
        assertEquals(LocalDateTime.of(2025, 1, 15, 18, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: yyyy-MM-dd HH:mm format")
    void parseUserDateTime_isoWithColon_parses() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2025-01-15 18:00");
        assertEquals(LocalDateTime.of(2025, 1, 15, 18, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: d/M/yyyy HHmm format")
    void parseUserDateTime_dmyNoColon_parses() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("15/1/2025 1800");
        assertEquals(LocalDateTime.of(2025, 1, 15, 18, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: d/M/yyyy HH:mm format")
    void parseUserDateTime_dmyWithColon_parses() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("15/1/2025 18:00");
        assertEquals(LocalDateTime.of(2025, 1, 15, 18, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: date-only yyyy-MM-dd becomes midnight")
    void parseUserDateTime_dateOnlyIso_midnight() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2025-01-15");
        assertEquals(LocalDateTime.of(2025, 1, 15, 0, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: date-only d/M/yyyy becomes midnight")
    void parseUserDateTime_dateOnlyDmy_midnight() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("15/1/2025");
        assertEquals(LocalDateTime.of(2025, 1, 15, 0, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: trims input")
    void parseUserDateTime_trimsInput() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("  2025-01-15 1800  ");
        assertEquals(LocalDateTime.of(2025, 1, 15, 18, 0), dt);
    }

    @Test
    @DisplayName("parseUserDateTime: invalid string throws DateTimeParseException")
    void parseUserDateTime_invalid_throws() {
        assertThrows(DateTimeParseException.class, () ->
                DateTimeUtil.parseUserDateTime("not-a-date"));
        assertThrows(DateTimeParseException.class, () ->
                DateTimeUtil.parseUserDateTime("2025/01/15 1800"));
    }

    /* ---------- formatForDisplay ---------- */ 

    @Test
    @DisplayName("formatForDisplay: midnight returns date-only format (MMM dd yyyy)")
    void formatForDisplay_midnight_returnsDateOnly() {
        LocalDateTime midnight = LocalDateTime.of(2025, 1, 15, 0, 0);
        assertEquals("Jan 15 2025", DateTimeUtil.formatForDisplay(midnight));
    }

    @Test
    @DisplayName("formatForDisplay: non-midnight returns date and time (MMM dd yyyy HH:mm)")
    void formatForDisplay_nonMidnight_returnsDateTime() {
        LocalDateTime afternoon = LocalDateTime.of(2025, 1, 15, 18, 30);
        assertEquals("Jan 15 2025 18:30", DateTimeUtil.formatForDisplay(afternoon));
    }

    @Test
    @DisplayName("formatForDisplay: 23:59 returns date and time")
    void formatForDisplay_lateNight_returnsDateTime() {
        LocalDateTime late = LocalDateTime.of(2025, 1, 15, 23, 59);
        assertEquals("Jan 15 2025 23:59", DateTimeUtil.formatForDisplay(late));
    }

    /* ---------- formatDateForDisplay ---------- */

    @Test
    @DisplayName("formatDateForDisplay: formats LocalDate as MMM dd yyyy")
    void formatDateForDisplay_formatsCorrectly() {
        LocalDate d = LocalDate.of(2025, 12, 25);
        assertEquals("Dec 25 2025", DateTimeUtil.formatDateForDisplay(d));
    }
}
