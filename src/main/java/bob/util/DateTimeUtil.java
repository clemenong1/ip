package bob.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateTimeUtil {
    private static final DateTimeFormatter OUTPUT_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter OUTPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    /**
     * Storage format kept consistent on disk.
     * Example: 2019-12-02 1800
     */
    public static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Parses user input into a LocalDateTime, accepting multiple formats.
     *
     * @param raw Raw date/time string from user input.
     * @return Parsed LocalDateTime object.
     * @throws DateTimeParseException If the input cannot be parsed in any supported format.
     */
    public static LocalDateTime parseUserDateTime(String raw) {
        String s = raw.trim();

        DateTimeFormatter[] dateTimeFormats = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
        };

        for (DateTimeFormatter f : dateTimeFormats) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        DateTimeFormatter[] dateOnlyFormats = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };

        for (DateTimeFormatter f : dateOnlyFormats) {
            try {
                LocalDate d = LocalDate.parse(s, f);
                return d.atStartOfDay(); // date-only -> 00:00
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        throw new DateTimeParseException("Unparseable date/time", s, 0);
    }

    /**
     * Formats a LocalDateTime for display.
     * Returns date-only format if time is 00:00, otherwise returns date+time format.
     *
     * @param dt LocalDateTime to format.
     * @return Formatted string for display.
     */
    public static String formatForDisplay(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.format(OUTPUT_DATE);
        }
        return dt.format(OUTPUT_DATE_TIME);
    }

    /**
     * Formats a LocalDate for display.
     *
     * @param date LocalDate to format.
     * @return Formatted string for display.
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(OUTPUT_DATE);
    }
}

