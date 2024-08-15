package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LogReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderService.class);

    @Value("${log.file.path}")
    private String logFilePath;

    public List<String> findByData(LogRequest logRequest) {
        List<String> logsByData = new ArrayList<>();
        List<String> matchedLines = findLineContainingKeyword(logRequest.getKeyword());
        for (String logLine : matchedLines) {
            String extractedDate = DateUtils.extractDateTime(logLine);
            if (extractedDate != null) {
                logRequest.setDate(extractedDate);
                logsByData.addAll(findByDate(logRequest));
            }
        }
        logsByData.sort(Comparator.comparing(line -> DateUtils.extractDateTime((String) line)).reversed());
        return logsByData;
    }

    private List<String> findLineContainingKeyword(String keyword) {
        List<String> logLinesFinded = new ArrayList<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(logFilePath, "r")) {
            long fileLength = randomAccessFile.length();
            if (fileLength == 0L) {
                LOGGER.warn("El archivo de log está vacío: {}", logFilePath);
                return logLinesFinded;
            }

            long pointer = 0;
            StringBuilder builder = new StringBuilder();
            while (pointer < fileLength) {
                randomAccessFile.seek(pointer);
                String line = randomAccessFile.readLine();

                if (line != null) {
                    if (line.contains(keyword)) {
                        logLinesFinded.add(line);
                    }
                    pointer += line.length() + System.lineSeparator().length(); // Move the pointer to the next line
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de log", e);
        }
        return logLinesFinded;
    }

    public List<String> findByDate(LogRequest logRequest) {
        List<String> logLines = new ArrayList<>();
        if (logRequest.getDate() == null) {
            LOGGER.warn("No valid date found in log request.");
            return logLines;
        }

        String dateBefore = DateUtils.adjustMilliseconds(logRequest.getDate(), -1);
        String dateAfter = DateUtils.adjustMilliseconds(logRequest.getDate(), 1);
        String[] dateFilters = {dateBefore, logRequest.getDate(), dateAfter};

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(logFilePath, "r")) {
            long fileLength = randomAccessFile.length();
            if (fileLength == 0L) {
                LOGGER.warn("El archivo de log está vacío: {}", logFilePath);
                return logLines;
            }

            long pointer = 0;
            StringBuilder builder = new StringBuilder();
            while (pointer < fileLength) {
                randomAccessFile.seek(pointer);
                String line = randomAccessFile.readLine();

                if (line != null) {
                    for (String dateFilter : dateFilters) {
                        if (line.contains(dateFilter)) {
                            logLines.add(line);
                            break;
                        }
                    }
                    pointer += line.length() + System.lineSeparator().length(); // Move the pointer to the next line
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de log", e);
        }
        logLines.sort(Comparator.comparing(line -> DateUtils.extractDateTime((String) line)).reversed());
        return logLines;
    }
}