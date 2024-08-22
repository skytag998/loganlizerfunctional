package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerByFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("log/files")
public class LogFilesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFilesController.class);
    @Autowired
    LogAnalyzerByFileService logAnalyzerByFileService;

    private List<String> files =new ArrayList<>();

    @GetMapping
    public String showForm (Model model) throws IOException {
        files = logAnalyzerByFileService.getFileList();
        model.addAttribute("files",files);
        return "logfilessearch";
    }
    //Obtiene datos por la fecha
    @PostMapping ("/bydate")
    public String getLogsByDate(LogSearchRequest logSearchRequest,Model model){
        List<String> logs = new ArrayList<>();
        try{
            logs=logAnalyzerByFileService.findByDate(logSearchRequest);
        }catch (IOException e){
            logs=new ArrayList<>();// se mantiene vacio para no generar error en la vista
            logs.add("Error en la fecha proporcionada: " +logSearchRequest.getDate()+" favor de revisarla ");
            LOGGER.error("Error :" ,e);
        }
        String logsFormatted = formatLogs(logs);
        model.addAttribute("files",files);
        model.addAttribute("results",logsFormatted);
        return "logresults";
    }
    @PostMapping("/keyword")
    public String getLogsFilesByKeyword (LogSearchRequest logSearchRequest, Model model){
        List<String> logs = new ArrayList<>();
        try {
            logs=logAnalyzerByFileService.findByKeyword(logSearchRequest);
        }catch (Exception e){
            logs = new ArrayList<>();
            logs.add("No se encontraron resultados con : "+logSearchRequest.getKeyword());
            LOGGER.error("Error :" ,e);
        }
        String logsFormatted = formatLogs(logs);
        model.addAttribute("files", files);
        model.addAttribute("results",logsFormatted);
        return "logresults";
    }

    @PostMapping("keyword-date")
    public String getLogsFilesByKeywordAndDate (LogSearchRequest logSearchRequest, Model model){
        List<String> logs = new ArrayList<>();

        try{
            logs = logAnalyzerByFileService.findByDateAndKeyword(logSearchRequest);
        }catch (IOException e){
            logs=new ArrayList<>(); //Vacia la lista si se lleno de error
            logs.add("No se encontraro resultados con los criterios dados: ");
            logs.add("Fecha: "+logSearchRequest.getDate());
            logs.add("Palabra clave: "+logSearchRequest.getKeyword());
            logs.add("Revisar que la transacción corresponda al archivo: "+logSearchRequest.getFile());
            LOGGER.error("Error :" ,e);

        }
        String logsFormatted = formatLogs(logs);
        model.addAttribute("files",files);
        model.addAttribute("results",logsFormatted);
        return "logresults";
    }

    private String formatLogs(List<String> logs ){
        return logs.stream().collect(Collectors.joining("\r\n"));
    }


}
