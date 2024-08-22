package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerLogByLapseTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/log/live/lapsetime")
public class LogLapseTimeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogLapseTimeController.class);
    @Autowired
    private LogAnalyzerLogByLapseTimeService logAnalyzerLogByLapseTimeService;

    @GetMapping
    public String showForm(){
        return "lapsetimelog";
    }
    @GetMapping("/last-minutes")
    public String getLogsByLapseTime (@RequestParam("minutes") int minutes, Model model){
        List <String> logLines = new ArrayList<>();
        try{
            logLines=logAnalyzerLogByLapseTimeService.getByLastMinutes(minutes);
        }catch (IOException e){
            logLines = new ArrayList<>();
            logLines.add("No se encontraron registros en el lapso de tiempo");
            LOGGER.error("No se encontraron registros");

        }
        String logsFormatted = formatLogs(logLines);
        model.addAttribute("results",logsFormatted);
        return "logresults";
    }



    // metodo para formatear los logs para el textearea en la vista de resultados
    private String formatLogs(List<String> logs){
        return logs.stream().collect(Collectors.joining("\r\n"));
    }

}
