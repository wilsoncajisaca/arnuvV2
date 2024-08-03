package com.core.arnuv.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
        response.sendRedirect("/auth/login?error=true");
        /*String errorMessage;
        if (exception.getClass().isAssignableFrom(LockedException.class)) {
            errorMessage = "Usuario actualmente deshabilitado";
        } else {
            errorMessage = "Usuario o contraseña incorrecta";
        }

        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/auth/login?error=" + encodedErrorMessage);*/
    }
}
