/*
*       Nueva Walmart México
*
*       Entity For LogRequest
*       Developed by:  Abdiel Mejia (VN577V7) [Softtek]
*       Date: 07-AGO-2024
*
*       Description:
*       Entity to store the rest request json
*
 */
package com.walmart.services.loganalyzer.LogAnalizerWalmart.Model;

public class LogRequest {

    private String keyword;
    private String date;
    private String action;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

        this.date=date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LogRequest() {
        this.keyword = keyword;
        this.date = date;
        this.action = action;
    }

}
