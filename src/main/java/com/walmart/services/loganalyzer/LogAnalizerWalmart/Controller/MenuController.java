package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping ("/")
public class MenuController {

    @GetMapping
    public String menuMain (){
        //Para mostrar el menu en raiz
        return "main";
    }
}
