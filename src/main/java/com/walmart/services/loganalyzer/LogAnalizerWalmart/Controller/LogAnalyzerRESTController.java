package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerByFileService;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerLogByLapseTimeService;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerLogOnLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/log/rest")
public class LogAnalyzerRESTController {

    @Value("${log.file.path}")
    private String logFilePath;
    @Autowired
    LogAnalyzerByFileService logAnalyzerByFileService;
    @Autowired
    LogAnalyzerLogByLapseTimeService logAnalyzerLogByLapseTimeService;
    @Autowired
    LogAnalyzerLogOnLiveService logAnalyzerLogOnLiveService;

    //prebas de By File
    @GetMapping("/files")
    public List<String> getFileList () throws IOException {
        List <String> listFIles =new ArrayList<>();
        listFIles = logAnalyzerByFileService.getFileList();
        return  listFIles;
    }
    @PostMapping(value = "/byolddate",consumes = "application/json", produces = "application/json")
    public List<String> getByDateOld (@RequestBody LogSearchRequest logRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        logLines= logAnalyzerByFileService.findByDate(logRequest);
        return logLines;
    }
    @PostMapping(value = "/byoldata",consumes = "application/json", produces = "application/json")
    public List<String> getByDataOld (@RequestBody LogSearchRequest logSearchRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        logLines = logAnalyzerByFileService.findByKeyword(logSearchRequest);
        return logLines;
    }
    @PostMapping(value = "/byolddateanddata",consumes = "application/json", produces = "application/json")
    public List<String> getByDateAndKeywordOld (@RequestBody LogSearchRequest logSearchRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        return logLines;
    }

    //#######################################################
    @PostMapping(value = "/loglivedate",consumes = "application/json", produces = "application/json")
    public List<String> getByDateLive (@RequestBody LogRequest logRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        logLines = logAnalyzerLogOnLiveService.filteredByDate(logRequest);
        return logLines;
    }
    @PostMapping(value = "/loglivedata",consumes = "application/json", produces = "application/json")
    public List<String> getByDataLive (@RequestBody LogRequest logRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        logLines = logAnalyzerLogOnLiveService.filteredByKeyword(logRequest);
        return logLines;
    }
    @PostMapping(value = "/loglivekeywordanddata",consumes = "application/json", produces = "application/json")
    public List<String> getByDateAndDataLive (@RequestBody LogRequest logRequest)throws IOException{
        List<String> logLines = new ArrayList<>();
        logLines = logAnalyzerLogOnLiveService.filteredByKeywordAndDate(logRequest);
        return logLines;
    }

    //######################################### By Lapse time #############################################
    @GetMapping("/lastminutes")
    public String getLogsByLastMinutes (@RequestParam ("minutes") int minutes ) throws IOException {
        List<String> logResults = new ArrayList<>();
        logResults = logAnalyzerLogByLapseTimeService.getByLastMinutes(minutes);
        String formatedLogs = formatLogs(logResults);
        return logResults.toString();
    }

    private String formatLogs(List<String> logs){
        return logs.stream().collect(Collectors.joining("\r\n"));
    }


}
