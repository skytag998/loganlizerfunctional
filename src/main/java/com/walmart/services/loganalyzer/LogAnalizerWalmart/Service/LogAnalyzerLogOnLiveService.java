package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.DateLogUtils;
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
public class LogAnalyzerLogOnLiveService {
    @Value("${log.file.path}")
    private String logFilePath;
    private static  final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerByFileService.class);

    public List<String> filteredByKeyword (LogRequest logRequest) throws IOException{
        List<String> logLines =readLog();
        List<String> logReturn = new ArrayList<>();
        List<String> coincidences = new ArrayList<>();
        for(String line :logLines){
            if(line.contains(logRequest.getKeyword())){
                coincidences.add(line);
            }
        }
        for(String match : coincidences){

            String extractedDate = DateLogUtils.extractDateTime(match);
            String [] filters = makeFilters(extractedDate);
            for (String filter : filters) {
                for (String line : logLines) {
                    if (line.contains(filter)) {
                        logReturn.add(line);
                    }
                }
            }
        }
        return logReturn;
    }

    public List<String> filteredByKeywordAndDate (LogRequest logRequest) throws IOException{
        List<String> logLines = readLog();
        List<String> logReturn = new ArrayList<>();
        String lineSelected = "";
        for(String line : logLines){
            if(line.contains(logRequest.getDate()) && (line.contains(logRequest.getKeyword()))){
                lineSelected= line;
            }
        }
        for(String filter : makeFilters(DateLogUtils.extractDateTime(lineSelected))){
            for(String line: logLines){
                if(line.contains(filter)){
                    logReturn.add(line);
                }
            }
        }
        logReturn.sort(Comparator.comparing(line -> DateLogUtils.extractDateTime(line)));
        return  logReturn;
    }

    public List<String> filteredByDate (LogRequest logRequest) throws IOException {
        List<String> logLines = readLog();
        List<String> logReturn = new ArrayList<>();

        String [] filers = makeFilters(logRequest.getDate());

        for (String filterDate : filers) {
            for (String line : logLines) {
                if (line.contains(filterDate)) {
                    logReturn.add(line);
                }
            }
        }
        logReturn.sort(Comparator.comparing(line -> DateLogUtils.extractDateTime(line)));
        return logReturn;
    }

    private List<String> readLog () throws IOException {
        List<String> lines = new ArrayList<>();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(logFilePath,"r")) {
            String line;
            while ((line = randomAccessFile.readLine()) != null) {
                lines.add(line);
            }

        }catch (IOException e ){
            LOGGER.error("Error al leer el archivo");
            throw  new IOException("Error al leer el archivo: "+logFilePath,e);
        }
        return lines;
    }
    private String [] makeFilters (String date){
        String dateBefore = DateLogUtils.subtractMillisecondsFromGivenTime(date,1);
        String dateAfter = DateLogUtils.addMillisecondsToGivenTime(date,1);
        String [] filters ={dateBefore,date,dateAfter};
        return filters;
    }
}
