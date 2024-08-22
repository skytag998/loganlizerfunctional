package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerLogOnLiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("log/live")
public class LiveLogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveLogController.class);
    @Autowired
    private LogAnalyzerLogOnLiveService logAnalyzerLogOnLiveService;

    //metodo endpoint para cargar formulario
    @GetMapping
    public String showForm(){
        return "loglivesearch";
    }
    @RequestMapping("/keyword")
    public String getLiveLogByKeyword (LogRequest logRequest, Model model){
        List<String> logs = new ArrayList<>();
        try{
            logs=logAnalyzerLogOnLiveService.filteredByKeyword(logRequest);
        }catch (IOException e){
            logs = new ArrayList<>();
            logs.add("No se encontraron coincidencias con: " +logRequest.getKeyword());
            LOGGER.error("Error: ", e);
        }
        String logFormatted = formatLogs(logs);
        model.addAttribute("results",logFormatted);
        return "logresults";
    }
    @RequestMapping("/by-date")
    public String getLiveLogByDate (LogRequest logRequest, Model model){
        List<String> logLines = new ArrayList<>();
        try{
            logLines= logAnalyzerLogOnLiveService.filteredByDate(logRequest);
        }catch (IOException e){
            logLines = new ArrayList<>();
            logLines.add("No se encontraron coincidencias con: " +logRequest.getDate());
            LOGGER.error("Error",e);
        }
        String logFormmated = formatLogs(logLines);
        model.addAttribute("results",logFormmated);
        return "logresults";
    }

    @RequestMapping("/keyword-date")
    public String getLiveLogByDateAndKeyword (LogRequest logRequest, Model model){
        List<String> logLines = new ArrayList<>();
        try {
            logLines=logAnalyzerLogOnLiveService.filteredByKeywordAndDate(logRequest);
        }catch (IOException e){
            logLines = new ArrayList<>();
            logLines.add("No se encontraron coincidencias con: Date:"+logRequest.getDate()+" Keyword: "+logRequest.getKeyword());
            logLines.add("Revise los criterios de busqueda");
        }
        String logFormatted = formatLogs(logLines);
        model.addAttribute("results",logFormatted);
        return "logresults";
    }


    // metodo para formatear los logs para el textearea en la vista de resultados
    private String formatLogs(List<String> logs){
        return logs.stream().collect(Collectors.joining("\r\n"));
    }

}
