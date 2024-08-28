package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.DateLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class LogAnalyzerByFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerByFileService.class);

    @Value("${log.directory.path}")
    private String logFileDirectory;

    public List<String> findByDateAndKeyword (LogSearchRequest logFileSearchRequest) throws IOException{
        String logPath = logFileDirectory+logFileSearchRequest.getFile();
        List<String> logLines = readLog(logPath);
        List<String> logReturn = new ArrayList<>();
        String lineSelected ="";
        for(String line : logLines){
            if(line.contains(logFileSearchRequest.getDate()) && (line.contains(logFileSearchRequest.getKeyword()))){
                lineSelected = line;
            }
        }
        for (String filter : makeFilters(DateLogUtils.extractDateTime(lineSelected))){
            for(String line : logLines){
                if(line.contains(filter)){
                    logReturn.add(line);
                }
            }
        }
        logReturn.sort(Comparator.comparing(line -> DateLogUtils.extractDateTime((String) line)));
        return logReturn;
    }

    public List<String> findByKeyword (LogSearchRequest logFileSearchRequest) throws IOException {
        String filePath = logFileDirectory+logFileSearchRequest.getFile();
        List<String> logLines = readLog(filePath);
        List<String> coincidences = new ArrayList<>();
        List<String> logReturn = new ArrayList<>();
        for (String line : logLines){
            if(line.contains(logFileSearchRequest.getKeyword())){
                coincidences.add(line);
            }
        }
        for(String match :coincidences ){
            String extractedDate = DateLogUtils.extractDateTime(match);
            String [] filters = makeFilters(extractedDate);
            for(String filter : filters){
                for(String line : logLines ){
                    if(line.contains(filter)){
                        logReturn.add(line);
                    }

                }
            }
        }
        logReturn.sort(Comparator.comparing(line -> DateLogUtils.extractDateTime((String) line)));
        return  logReturn;
    }

    public List<String> findByDate(LogSearchRequest logFileSearchRequest) throws IOException {
        String fileRoute = logFileDirectory + logFileSearchRequest.getFile();
        List<String> lines = readLog(fileRoute);
        Set<String> logReturnSet = new HashSet<>(); // Usamos un Set para evitar duplicados
        String[] filters = makeFilters(logFileSearchRequest.getDate());

        for (String filterDate : filters) {
            for (String line : lines) {
                if (line.contains(filterDate)) {
                    logReturnSet.add(line); // Agregamos al Set para evitar duplicados
                }
            }
        }

        List<String> logReturn = new ArrayList<>(logReturnSet); // Convertimos el Set a List
        logReturn.sort(Comparator.comparing(line -> DateLogUtils.extractDateTime(line)));
        return logReturn;
    }

    private String [] makeFilters (String date){
        String dateBefore = DateLogUtils.subtractMillisecondsFromGivenTime(date,1);
        String dateAfter = DateLogUtils.addMillisecondsToGivenTime(date,1);
        String [] filters = {dateBefore,date,dateAfter};
        return filters;
    }

    private List<String> readLog (String fileRoute) throws  IOException{
        List<String> lines = new ArrayList<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileRoute,"r")){
            String line;
            while ((line= randomAccessFile.readLine()) != null){
                lines.add(line);
            }
        }catch (IOException e){
            LOGGER.error("Error al leer el archivo");
            throw new IOException("Error al leer el archivo"+ logFileDirectory, e);
        }

        return lines;

    }

    public List<String> getFileList () throws IOException {
        if(logFileDirectory == null || logFileDirectory.trim().isEmpty()){
            throw  new  IllegalArgumentException("El directorio no puede ser nulo o vacio");
        }
        Path directory = Paths.get(logFileDirectory);
        if(!Files.exists(directory) || !Files.isDirectory(directory)){
            throw new IllegalArgumentException("El directorio especifico no existe o no es un directorio valido");
        }
        try (Stream<Path> pathStream = Files.list(directory) ){
            return pathStream
                    .map(path -> path.getFileName().toString())
                    .filter(filename -> filename.startsWith("SystemOut") && filename.endsWith(".log"))
                    .collect(Collectors.toList());

        }catch (IOException e ){
            LOGGER.error("Error al obtener la lista de los archivos del directorio: "+logFileDirectory, e);
            throw  e;
        }
    }

}
