package com.core.arnuv.controller;

import com.core.arnuv.jwt.IJwtService;
import com.core.arnuv.request.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "/login";
    }

    @PostMapping("/login-access")
    public String authenticateUser(@ModelAttribute("loginRequest") LoginRequest loginRequest, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/admin/welcome";
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("message", "Credenciales incorrectas. Por favor, intenta de nuevo.");
            return "redirect:/auth/login";
        }catch (InternalAuthenticationServiceException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/auth/login";
        }
        catch (Exception e) {
            log.error("Error durante la autenticación: ", e);
            redirectAttributes.addFlashAttribute("message", "Ha ocurrido un error. Por favor, intenta más tarde.");
            return "redirect:/auth/login";
        }
    }
}