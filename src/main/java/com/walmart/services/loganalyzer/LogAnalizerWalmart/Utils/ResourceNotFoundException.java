package com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils;

public class ResourceNotFoundException extends RuntimeException {
    //Para excepciones más detalladas
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
