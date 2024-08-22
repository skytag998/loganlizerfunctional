package com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateLogUtils {
    // El formato actualizado para el nuevo formato de fecha
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/yy HH:mm:ss:SSS");
    private static final String DATE_REGEX = "\\[(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+:\\d+)";
    private static final Logger LOGGER = LoggerFactory.getLogger(DateLogUtils.class);

    public static String addMilliseconds (String date, int milliseconds){
        LocalDateTime dateTime = LocalDateTime.parse(date,DATE_TIME_FORMATTER);
        dateTime = dateTime.plus(Duration.ofMillis(milliseconds));
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String substractMilliseconds (String date, int millis){
        LocalDateTime dateTime = LocalDateTime.parse(date,DATE_TIME_FORMATTER);
        dateTime = dateTime.minus(Duration.ofMillis(millis));

        return dateTime.format(DATE_TIME_FORMATTER);
    }

    // Extraer la fecha y hora del log usando regex
    public static String extractDateTime(String logLine) {
        Pattern pattern = Pattern.compile(DATE_REGEX);
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String addMillisecondsToGivenTime(String date, int millisecondsToAdd) {
        // Dividir la fecha en partes
        String[] parts = date.split(" ");
        String datePart = parts[0];
        String timePart = parts[1];

        // Dividir la hora, minutos, segundos y milisegundos
        String[] timeParts = timePart.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        int milliseconds = Integer.parseInt(timeParts[3]);

        // Sumar los milisegundos
        milliseconds += millisecondsToAdd;
        if (milliseconds >= 1000) {
            seconds += milliseconds / 1000;
            milliseconds %= 1000;
        }
        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }
        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }

        // Formatear el resultado sin ceros adicionales
        return String.format("%s %d:%02d:%02d:%03d", datePart, hours, minutes, seconds, milliseconds);
    }

    public static String subtractMillisecondsFromGivenTime(String date, int millisecondsToSubtract) {
        // Dividir la fecha en partes
        String[] parts = date.split(" ");
        String datePart = parts[0];
        String timePart = parts[1];

        // Dividir la hora, minutos, segundos y milisegundos
        String[] timeParts = timePart.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        int milliseconds = Integer.parseInt(timeParts[3]);

        // Restar los milisegundos
        milliseconds -= millisecondsToSubtract;
        if (milliseconds < 0) {
            seconds += milliseconds / 1000;
            milliseconds = (milliseconds % 1000 + 1000) % 1000;
        }
        if (seconds < 0) {
            minutes += seconds / 60;
            seconds = (seconds % 60 + 60) % 60;
        }
        if (minutes < 0) {
            hours += minutes / 60;
            minutes = (minutes % 60 + 60) % 60;
        }

        // Formatear el resultado sin ceros adicionales
        return String.format("%s %d:%02d:%02d:%03d", datePart, hours, minutes, seconds, milliseconds);
    }
    public static String subtractMinutesFromGivenTime(String dateTimeString, int minutesToSubtract) {
        String[] dateTimeParts = dateTimeString.split(" ");
        String[] dateParts = dateTimeParts[0].split("/");
        String[] timeParts = dateTimeParts[1].split(":");

        // Extraer componentes de tiempo
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        int milliseconds = Integer.parseInt(timeParts[3]);

        // Restar minutos
        minutes -= minutesToSubtract;

        // Ajustar minutos y horas
        if (minutes < 0) {
            hours -= (Math.abs(minutes) / 60 + 1);
            minutes = 60 - Math.abs(minutes) % 60;
            if (hours < 0) {
                hours = 24 + (hours % 24); // Ajustar si las horas son negativas
            }
        }

        // Formatear para evitar ceros innecesarios
        String formattedDateTime = String.format("%d/%d/%d %d:%d:%d:%d",
                Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]),
                hours, minutes, seconds, milliseconds);

        return formattedDateTime;
    }
    public static boolean isDateWithinRange(String lineDate, String minutesAgo, String nowTime) {
        if (lineDate == null || minutesAgo == null || nowTime == null) {
            return false;
        }

        // Extraer partes de la fecha
        String[] lineParts = lineDate.split(" ");
        String[] minutesAgoParts = minutesAgo.split(" ");
        String[] nowParts = nowTime.split(" ");

        // Convertir las partes a enteros para comparación
        int lineHours = Integer.parseInt(lineParts[1].split(":")[0]);
        int lineMinutes = Integer.parseInt(lineParts[1].split(":")[1]);
        int lineSeconds = Integer.parseInt(lineParts[1].split(":")[2]);
        int lineMilliseconds = Integer.parseInt(lineParts[1].split(":")[3]);

        int minutesAgoHours = Integer.parseInt(minutesAgoParts[1].split(":")[0]);
        int minutesAgoMinutes = Integer.parseInt(minutesAgoParts[1].split(":")[1]);
        int minutesAgoSeconds = Integer.parseInt(minutesAgoParts[1].split(":")[2]);
        int minutesAgoMilliseconds = Integer.parseInt(minutesAgoParts[1].split(":")[3]);

        int nowHours = Integer.parseInt(nowParts[1].split(":")[0]);
        int nowMinutes = Integer.parseInt(nowParts[1].split(":")[1]);
        int nowSeconds = Integer.parseInt(nowParts[1].split(":")[2]);
        int nowMilliseconds = Integer.parseInt(nowParts[1].split(":")[3]);

        // Comparar fechas
        boolean isAfterOrEqualToStart = (lineHours > minutesAgoHours) ||
                (lineHours == minutesAgoHours && lineMinutes > minutesAgoMinutes) ||
                (lineHours == minutesAgoHours && lineMinutes == minutesAgoMinutes && lineSeconds > minutesAgoSeconds) ||
                (lineHours == minutesAgoHours && lineMinutes == minutesAgoMinutes && lineSeconds == minutesAgoSeconds && lineMilliseconds >= minutesAgoMilliseconds);

        boolean isBeforeOrEqualToEnd = (lineHours < nowHours) ||
                (lineHours == nowHours && lineMinutes < nowMinutes) ||
                (lineHours == nowHours && lineMinutes == nowMinutes && lineSeconds < nowSeconds) ||
                (lineHours == nowHours && lineMinutes == nowMinutes && lineSeconds == nowSeconds && lineMilliseconds <= nowMilliseconds);

        return isAfterOrEqualToStart && isBeforeOrEqualToEnd;
    }

}
