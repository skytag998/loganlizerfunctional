package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import com.walmart.services.loganalyzer.LogAnalizerWalmart.Service.LogAnalyzerOldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/websphare/log/files")
public class LogAnalyzerOldLogsController {

    @Autowired
    private LogAnalyzerOldService logAnalyzerOldService;

    @GetMapping
    public String showSearchForm(Model model) {
        try {
            List<String> files = logAnalyzerOldService.getFileList();
            model.addAttribute("files", files);
            model.addAttribute("searchRequest", new LogSearchRequest());
        } catch (IOException e) {
            model.addAttribute("error", "No se pudo cargar los archivos: " + e.getMessage());
        }
        return "searchForm";
    }

    @PostMapping("/searchByDate")
    public String searchByDate(@ModelAttribute LogSearchRequest request, Model model) {
        try {
            List<String> lines = logAnalyzerOldService.filterByDate(request);
            model.addAttribute("searchResults", lines != null ? String.join("\n", lines) : "");
            model.addAttribute("files", logAnalyzerOldService.getFileList());
            model.addAttribute("searchRequest", request);
        } catch (IOException e) {
            model.addAttribute("error", "Error al realizar la búsqueda: " + e.getMessage());
        }
        return "searchResults";
    }

    @PostMapping("/searchByKeyword")
    public String searchByKeyword(@ModelAttribute LogSearchRequest request, Model model) {
        try {
            List<String> lines = logAnalyzerOldService.filterByKeyword(request);
            model.addAttribute("searchResults", lines != null ? String.join("\n", lines) : "");
            model.addAttribute("files", logAnalyzerOldService.getFileList());
            model.addAttribute("searchRequest", request);
        } catch (IOException e) {
            model.addAttribute("error", "Error al realizar la búsqueda: " + e.getMessage());
        }
        return "searchResults";
    }

    @PostMapping("/searchAll")
    public String searchAll(@ModelAttribute LogSearchRequest request, Model model) {
        try {
            List<String> lines = logAnalyzerOldService.filterByDateAndKeyword(request);
            model.addAttribute("searchResults", lines != null ? String.join("\n", lines) : "");
            model.addAttribute("files", logAnalyzerOldService.getFileList());
            model.addAttribute("searchRequest", request);
        } catch (IOException e) {
            model.addAttribute("error", "Error al realizar la búsqueda: " + e.getMessage());
        }
        return "searchResults";
    }
}