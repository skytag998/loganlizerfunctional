package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/websphare/rest")
public class LogAnalyzerRESTController {

    @Value("${log.file.path}")
    private String logFilePath;
    @Autowired
    LogReaderService logReaderService;

    @PostMapping(value = "/log",consumes = "application/json", produces = "application/json")
    public List<String> getLogLinesByDate (@RequestBody LogRequest logRequest){
        return logReaderService.findByDate(logRequest);
    }
    @PostMapping(value = "/log/data",consumes = "application/json", produces = "application/json")
    public List<String> getLogLinesByData (@RequestBody LogRequest logRequest){
        return logReaderService.findByData(logRequest);
    }

}
