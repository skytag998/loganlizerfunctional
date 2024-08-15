package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.DTO.LogResponse;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerByMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/websphare/live")
public class LogAnalyzerByLastMinutesController {
    @Autowired
    LogAnalyzerByMinutesService logAnalyzerByMinutesService;

    @GetMapping("/log/byminutes")
    public String showForm (){
        return "logByMinutes";
    }

    @PostMapping("/log/byminutes")
    public String getLogsByMinutes (@RequestParam("minutes") int minutes, Model model){
        List<String> logLines = logAnalyzerByMinutesService.getLastLogsByMinutes(minutes);
        model.addAttribute("logs",logLines);
        model.addAttribute("minutes",minutes);
        return "logByMinutes";
    }
}