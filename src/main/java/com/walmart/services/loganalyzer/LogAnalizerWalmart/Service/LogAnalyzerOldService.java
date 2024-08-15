package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogAnalyzerOldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerOldService.class);

    @Value("${log.directory.path}")
    private String directoryPath;

    private RandomAccessFile logFile;
    private String logFilePath;
    private List<String> logFileCharged;

    // Método para listar archivos en el directorio
    public List<String> getFileList() throws IOException {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IllegalArgumentException("El directorio no puede ser nulo o vacío.");
        }

        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("El directorio especificado no existe o no es un directorio válido.");
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> fileName.startsWith("SystemOut") && fileName.endsWith(".log"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Error al obtener la lista de archivos del directorio: " + directoryPath, e);
            throw e;
        }
    }

    // Método para cargar el archivo seleccionado
    private void loadFile(String fileName) throws IOException {
        logFilePath = directoryPath + fileName;
        logFile = new RandomAccessFile(logFilePath, "r");
    }

    // Método para cerrar el archivo
    private void closeFile() {
        if (logFile != null) {
            try {
                logFile.close();
            } catch (IOException e) {
                LOGGER.error("Error closing file", e);
            }
        }
    }

    // Método para obtener líneas del archivo
    private List<String> readLines() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = logFile.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }



    // Método para filtrar por fecha
    public List<String> filterByDate(LogSearchRequest request) throws IOException {
        loadFile(request.getFile());
        List<String> allLines = readLines();
        closeFile();
        System.out.println(request.getDate());
        List<String> filteredLines = new ArrayList<>();
        String dateBefore = DateUtils.substractMilliseconds(request.getDate(), 1);
        String dateAfter = DateUtils.addMilliseconds(request.getDate(), 1);

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            String lineDate = DateUtils.extractDateTime(line);
            if (lineDate != null && DateUtils.isDateWithinRange(lineDate, dateBefore, dateAfter)) {
                filteredLines.add(line); // Añadir línea actual
                // Añadir línea anterior si existe
                if (i > 0) {
                    filteredLines.add(allLines.get(i - 1));
                }
                // Añadir línea siguiente si existe
                if (i < allLines.size() - 1) {
                    filteredLines.add(allLines.get(i + 1));
                }
            }
        }
        return filteredLines;
    }

    // Método para filtrar por keyword
    public List<String> filterByKeyword(LogSearchRequest request) throws IOException {
        loadFile(request.getFile());
        List<String> allLines = readLines();
        closeFile();

        List<String> filteredLines = new ArrayList<>();
        String keyword = request.getKeyword();
        Set<Integer> addedIndices = new HashSet<>();

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (line.contains(keyword)) {
                // Añadir línea actual
                if (!addedIndices.contains(i)) {
                    filteredLines.add(line);
                    addedIndices.add(i);
                }
                // Añadir línea anterior si existe y no está ya añadida
                if (i > 0 && !addedIndices.contains(i - 1)) {
                    filteredLines.add(allLines.get(i - 1));
                    addedIndices.add(i - 1);
                }
                // Añadir línea siguiente si existe y no está ya añadida
                if (i < allLines.size() - 1 && !addedIndices.contains(i + 1)) {
                    filteredLines.add(allLines.get(i + 1));
                    addedIndices.add(i + 1);
                }
            }
        }
        return filteredLines;
    }

    // Método para filtrar por fecha y palabra clave
    public List<String> filterByDateAndKeyword(LogSearchRequest request) throws IOException {
        loadFile(request.getFile());
        List<String> allLines = readLines();
        closeFile();

        List<String> filteredLines = new ArrayList<>();
        String dateBefore = DateUtils.substractMilliseconds(request.getDate(), 1);
        String dateAfter = DateUtils.addMilliseconds(request.getDate(), 1);
        String keyword = request.getKeyword();

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            String lineDate = DateUtils.extractDateTime(line);
            if (lineDate != null && DateUtils.isDateWithinRange(lineDate, dateBefore, dateAfter) && line.contains(keyword)) {
                filteredLines.add(line); // Añadir línea actual
                // Añadir línea anterior si existe
                if (i > 0) {
                    filteredLines.add(allLines.get(i - 1));
                }
                // Añadir línea siguiente si existe
                if (i < allLines.size() - 1) {
                    filteredLines.add(allLines.get(i + 1));
                }
            }
        }
        return filteredLines;
    }
}