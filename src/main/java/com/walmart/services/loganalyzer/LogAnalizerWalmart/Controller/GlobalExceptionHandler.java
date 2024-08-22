package com.walmart.services.loganalyzer.LogAnalizerWalmart.Controller;

import com.walmart.services.loganalyzer.LogAnalizerWalmart.Utils.InternalServerErrorException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "El recurso que buscas no se encontró.");
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleInternalServerErrorException(InternalServerErrorException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Ha ocurrido un error interno en el servidor.");
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "La página que buscas no existe.");
        mav.setViewName("error");
        return mav;
    }
}