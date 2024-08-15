package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/websphare/live")
public class LogAnalyzerController {

    @Autowired
    private LogReaderService logReaderService;

    @GetMapping("/log")
    public String showLogForm(Model model) {
        model.addAttribute("logRequest", new LogRequest());
        model.addAttribute("logLines", new ArrayList<String>());
        return "logFormAndResults"; // Nombre de la vista Thymeleaf
    }

    @PostMapping("/log/date")
    public String getLogLinesByDate(@ModelAttribute LogRequest logRequest, Model model) {
        List<String> logLines = logReaderService.findByDate(logRequest);
        String logLinesAsString = String.join("\r\n",logLines);
        model.addAttribute("logLines", logLinesAsString);
        return "logFormAndResults"; // Misma vista para resultados
    }

    @PostMapping("/log/data")
    public String getLogLinesByData(@ModelAttribute LogRequest logRequest, Model model) {
        List<String> logLines = logReaderService.findByData(logRequest);
        String logLinesAsString = String.join("\r\n",logLines);
        model.addAttribute("logLines", logLinesAsString);
        return "logFormAndResults"; // Misma vista para resultados
    }
}