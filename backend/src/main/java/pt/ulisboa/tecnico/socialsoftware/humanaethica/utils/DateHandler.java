package pt.ulisboa.tecnico.socialsoftware.humanaethica.utils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHandler {

    private DateHandler() {
    }

    /**
     * Converts LocalDateTime to ISO8601 string format
     */
    public static String toISOString(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return ZonedDateTime.of(time, ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Converts LocalDateTime to ISO8601 string format
     */
    // NOTE: it generates Lisbon time, it is used for the csv file, to be customizable it has to receive a local from the browser
    public static String toHumanReadableString(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ZonedDateTime.of(time, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Lisbon")).format(formatter);
    }

    /**
     * Converts ISO8601 string format to LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String date) {
        if (date == null || date.isBlank()) return null;

        try {
            // Try parsing as ZonedDateTime (e.g., "2025-05-16T08:30:00Z" or with offset)
            return ZonedDateTime.parse(date)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (DateTimeParseException ex1) {
            try {
                // Try parsing as LocalDateTime (e.g., "2025-05-16T08:30")
                return LocalDateTime.parse(date);
            } catch (DateTimeParseException ex2) {
                return null;
            }
        }
    }

    /**
     * Frontend converts ISO8601 format to yyyy-MM-dd HH:mm in client timezone
     * <p>
     * If it returns that format, it means the date wasn't changed
     * Because the component v-datetime-picker returns ISO8601 string format
     * <p>
     * Do not convert this string to LocalDateTime because it does not have timezone information
     */
    public static boolean isValidDateFormat(String string) {
        return string == null || !string.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
