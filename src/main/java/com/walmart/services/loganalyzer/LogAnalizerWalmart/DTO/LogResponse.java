package com.walmart.services.loganalyzer.LogAnalizerWalmart.DTO;

import java.util.List;


public class LogResponse {

    private List<String> lines;

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public LogResponse() {
        this.lines = lines;
    }
}
