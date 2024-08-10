package com.core.arnuv.handlers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", "Access is denied");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "landing/access-denied"; // nombre de la vista Thymeleaf
    }
}
