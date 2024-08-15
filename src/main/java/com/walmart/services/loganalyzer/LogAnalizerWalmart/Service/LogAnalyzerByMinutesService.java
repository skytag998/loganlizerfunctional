package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.DTO.LogResponse;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogAnalyzerByMinutesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerByMinutesService.class);

    @Value("${log.file.path}")
    private String logFilePath;

    public List<String> getLastLogsByMinutes(int minutes) {
        List<String> logLines = new ArrayList<>();
        String lastLine = getLastLogLine();

        if (lastLine != null) {
            String extractedDate = DateUtils.extractDateTime(lastLine);

            if (extractedDate != null) {
                // La fecha actual se toma de la última línea del log
                String now = extractedDate;
                String minutesAgo = DateUtils.getMinutesAgo(extractedDate,minutes);

                System.out.println("minutos: " + minutes + " now: " + now + " min ago: " + minutesAgo + " ext: " + extractedDate);

                try (RandomAccessFile randomAccessFile = new RandomAccessFile(logFilePath, "r")) {
                    long fileLength = randomAccessFile.length();
                    if (fileLength == 0L) {
                        LOGGER.warn("El archivo de log está vacío: {}", logFilePath);
                    }

                    long pointer = 0;
                    StringBuilder builder = new StringBuilder();
                    while (pointer < fileLength) {
                        randomAccessFile.seek(pointer);
                        String line = randomAccessFile.readLine();

                        if (line != null) {
                            String logDataTime = DateUtils.extractDateTime(line);
                            if (logDataTime != null) {
                                // Verificar si la línea está dentro del rango de tiempo
                                if (DateUtils.isDateWithinRange(logDataTime, minutesAgo, now)) {
                                    logLines.add(line);
                                } else if (DateUtils.stringToLocalDateTime(logDataTime).isAfter(DateUtils.stringToLocalDateTime(now))) {
                                    // Salir del bucle si la fecha de la línea es mayor que `now`
                                    break;
                                }
                            }
                            pointer += line.length() + System.lineSeparator().length(); // Mover el puntero a la siguiente línea
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("Error al abrir el archivo log ", e);
                }
            } else {
                LOGGER.error("No se pudo extraer la fecha de la última línea del log");
            }
        } else {
            LOGGER.error("El archivo de Log está vacío o no se pudo leer");
        }

        return logLines;
    }

    private String getLastLogLine() {
        String lastLine = null;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(logFilePath, "r")) {
            long fileLength = randomAccessFile.length();
            if (fileLength == 0L) {
                LOGGER.warn("El archivo de log está vacío: {}", logFilePath);
                return null;
            }

            long pointer = fileLength - 1;
            randomAccessFile.seek(pointer);

            // Leer desde el final del archivo hacia el principio
            StringBuilder builder = new StringBuilder();
            int readByte;
            while (pointer >= 0) {
                readByte = randomAccessFile.readByte();
                if (readByte == '\n' || readByte == '\r') {
                    if (builder.length() > 0) {
                        lastLine = builder.reverse().toString();
                        break;
                    }
                } else {
                    builder.append((char) readByte);
                }
                pointer--;
                randomAccessFile.seek(pointer);
            }

            if (lastLine == null) {
                // Si el archivo tiene solo una línea
                lastLine = builder.reverse().toString();
            }
            LOGGER.debug("Última línea del log: {}", lastLine);

        } catch (IOException e) {
            LOGGER.error("Error al abrir o leer el archivo: {}", logFilePath, e);
        }
        return lastLine;
    }
}