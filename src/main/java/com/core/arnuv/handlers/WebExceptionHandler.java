package com.core.arnuv.handlers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", "Access is denied");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "landing/access-denied";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", "Page not found");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "landing/access-denied";
    }
}