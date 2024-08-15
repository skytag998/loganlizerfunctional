package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class LogReaderServiceTest {

    private LogReaderService logReaderService;
    //variables de la ruta
    String userId="vn577v7";
    private String logRoute="C:\\\\Users\\\\"+userId+"\\\\Desktop\\\\SystemOut.log";
    //Variables para el test


    @Test
    public void testFindByData() throws IOException {
        try(BufferedReader bufferedReader1 = new BufferedReader(new FileReader(logRoute))){
            String line;
            while ((line = bufferedReader1.readLine())!= null){
                System.out.println(line);
            }
        }
    }

}


