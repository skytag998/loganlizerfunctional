package com.walmart.services.loganalyzer.LogAnalizerWalmart.Service;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Model.LogSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicePrueba {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerOldService.class);

    @Value("${log.directory.path}")
    private String directoryPath;

    public  List<String> readFile () throws IOException{
        String logpath = directoryPath+"SystemOut.log";
        List<String> lines = new ArrayList<>();
        try ( RandomAccessFile file = new RandomAccessFile(logpath,"r")){
            String line;
            while ((line=file.readLine()) != null){
                lines.add(line);
            }
        }
        return lines;
    }

}
