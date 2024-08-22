package com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateLogUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateLogUtils.class);
    private static final String DATE_REGEX_WITH_MILLIS = "\\[(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+:\\d+)";
    private static final String DATE_REGEX_NO_MILLIS = "\\[(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+)]";

    public static String addMillisecondsToGivenTime(String date, int millisecondsToAdd) {
        try {
            return adjustTime(date, millisecondsToAdd);
        } catch (Exception e) {
            LOGGER.error("Error al agregar milisegundos a la fecha: {}", date, e);
            return null;
        }
    }

    public static String subtractMillisecondsFromGivenTime(String date, int millisecondsToSubtract) {
        try {
            return adjustTime(date, -millisecondsToSubtract);
        } catch (Exception e) {
            LOGGER.error("Error al restar milisegundos a la fecha: {}", date, e);
            return null;
        }
    }

    public static String subtractMinutesFromGivenTime(String dateTimeString, int minutesToSubtract) {
        try {
            return adjustTime(dateTimeString, -minutesToSubtract * 60 * 1000);
        } catch (Exception e) {
            LOGGER.error("Error al restar minutos a la fecha: {}", dateTimeString, e);
            return null;
        }
    }

    public static String extractDateTime(String logLine) {
        try {
            Pattern pattern = Pattern.compile(DATE_REGEX_WITH_MILLIS);
            Matcher matcher = pattern.matcher(logLine);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                pattern = Pattern.compile(DATE_REGEX_NO_MILLIS);
                matcher = pattern.matcher(logLine);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error al extraer la fecha y hora del log: {}", logLine, e);
        }
        return null;
    }

    public static boolean isDateWithinRange(String lineDate, String minutesAgo, String nowTime) {
        try {
            if (lineDate == null || minutesAgo == null || nowTime == null) {
                return false;
            }

            // Convertir las partes de tiempo a enteros para comparación
            String[] lineTimeParts = lineDate.split(" ")[1].split(":");
            String[] minutesAgoTimeParts = minutesAgo.split(" ")[1].split(":");
            String[] nowTimeParts = nowTime.split(" ")[1].split(":");

            // Comparar las fechas sin y con milisegundos
            return compareTimeParts(lineTimeParts, minutesAgoTimeParts) >= 0 &&
                    compareTimeParts(lineTimeParts, nowTimeParts) <= 0;

        } catch (Exception e) {
            LOGGER.error("Error al comparar la fecha {} dentro del rango: {} - {}", lineDate, minutesAgo, nowTime, e);
            return false;
        }
    }

    private static String adjustTime(String dateTimeString, int millisecondsAdjustment) {
        String[] parts = dateTimeString.split(" ");
        String datePart = parts[0];
        String timePart = parts[1];

        String[] timeParts = timePart.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        int milliseconds = timeParts.length == 4 ? Integer.parseInt(timeParts[3]) : 0;

        milliseconds += millisecondsAdjustment;
        if (milliseconds >= 1000) {
            seconds += milliseconds / 1000;
            milliseconds %= 1000;
        } else if (milliseconds < 0) {
            seconds += (milliseconds / 1000) - 1;
            milliseconds = (milliseconds % 1000 + 1000) % 1000;
        }

        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        } else if (seconds < 0) {
            minutes += (seconds / 60) - 1;
            seconds = (seconds % 60 + 60) % 60;
        }

        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        } else if (minutes < 0) {
            hours += (minutes / 60) - 1;
            minutes = (minutes % 60 + 60) % 60;
        }

        return timeParts.length == 4
                ? String.format("%s %d:%02d:%02d:%03d", datePart, hours, minutes, seconds, milliseconds)
                : String.format("%s %d:%02d:%02d", datePart, hours, minutes, seconds);
    }

    private static int compareTimeParts(String[] timeParts1, String[] timeParts2) {
        for (int i = 0; i < timeParts1.length; i++) {
            int diff = Integer.parseInt(timeParts1[i]) - Integer.parseInt(timeParts2[i]);
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
}