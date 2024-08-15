package com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    // El formato actualizado para el nuevo formato de fecha
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/yy HH:mm:ss:SSS");
    private static final String DATE_REGEX = "\\[(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+:\\d+)";

    // Ajustar los milisegundos a la fecha dada
    public static String adjustMilliseconds(String date, int milliseconds) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        dateTime = dateTime.plusNanos(milliseconds * 1_000_000); // Convertir milisegundos a nanosegundos
        return dateTime.format(DATE_TIME_FORMATTER);
    }

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

    // Convertir una fecha en formato String a LocalDateTime
    public static LocalDateTime stringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
    }

    // Obtener la fecha actual en formato String
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    // Obtener la fecha de hace 'minutes' minutos en formato String
    public static String getMinutesAgo(String dateTimeString, int minutes) {
        try {
            // Parsear la fecha y hora proporcionadas
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);

            // Restar los minutos
            LocalDateTime minutesAgo = dateTime.minusMinutes(minutes);

            // Formatear la fecha y hora en el formato deseado
            return minutesAgo.format(DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El formato de la fecha es inválido: " + e.getMessage(), e);
        }
    }

    // Verificar si una fecha está dentro del rango dado
    public static boolean isDateWithinRange(String lineDate, String minutesAgo, String nowTime) {
        try {
            LocalDateTime lineDateTime = LocalDateTime.parse(lineDate, DATE_TIME_FORMATTER);
            LocalDateTime minutesAgoDateTime = LocalDateTime.parse(minutesAgo, DATE_TIME_FORMATTER);
            LocalDateTime nowDateTime = LocalDateTime.parse(nowTime, DATE_TIME_FORMATTER);
            return (lineDateTime.isAfter(minutesAgoDateTime) || lineDateTime.isEqual(minutesAgoDateTime))
                    && (lineDateTime.isBefore(nowDateTime) || lineDateTime.isEqual(nowDateTime));
        } catch (DateTimeParseException e) {
            // Manejar la excepción según sea necesario
            System.err.println("Error al analizar la fecha: " + e.getMessage());
            return false;
        }
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
}