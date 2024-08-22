package com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils;

//Aqui se crean excepciones mas especificas
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}