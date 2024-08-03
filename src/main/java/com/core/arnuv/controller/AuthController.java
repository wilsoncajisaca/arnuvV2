package com.core.arnuv.controller;

import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.service.IUsuarioDetalleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private IUsuarioDetalleService userService;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request,
                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/auth/default";
        }
        HttpSession session = request.getSession(false);
        String errorMessage = Strings.EMPTY;

        if (session != null) {
            AuthenticationException exception = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (exception != null) {
                errorMessage = exception.getMessage();
                // Limpia la excepción después de manejarla
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
        }

        if (!errorMessage.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.addCookie(new Cookie("JSESSIONID", null));
        return "redirect:/auth/login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        this.setUserInSession(request);
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/content/welcome";
        }
        return "redirect:/content/welcome";
    }

    private void setUserInSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Usuariodetalle user = (Usuariodetalle) session.getAttribute("loggedInUser");
        if(user == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            user = userService.buscarPorEmailOrUserName(auth.getName());
            session.setAttribute("loggedInUser", user.getIdpersona());
        }
    }
}