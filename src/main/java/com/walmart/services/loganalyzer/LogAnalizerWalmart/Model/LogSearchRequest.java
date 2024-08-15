package com.walmart.services.loganalyzer.LogAnalizerWalmart.Model;

public class LogSearchRequest {

    private String file;
    private String keyword;
    private String date;
    //Getters
    public String getFile() {
        return file;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDate() {
        return date;
    }
    //Setters


    public void setFile(String file) {
        this.file = file;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
