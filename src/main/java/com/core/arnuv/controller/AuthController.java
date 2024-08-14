package com.core.arnuv.controller;

import com.core.arnuv.model.MenuItem;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.IMenuService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUsuarioDetalleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final IUsuarioDetalleService userService;
    private final IMenuService menuService;
    private final IPersonaDetalleService servicioPersonaDetalle;

    private final JavaMailSender mailSender;

    @GetMapping("/login")

    public String login(Model model, HttpServletRequest request,
                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/auth/default";
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            AuthenticationException exception = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (exception != null) {
                model.addAttribute("errorMessage", exception.getMessage());
                // Limpia la excepción después de manejarla
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
        }
        return "/landing/login";
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
        if (request.isUserInRole("ADMIN"))
            log.info("Ud es administrador");

        if (request.isUserInRole("CLIENTE"))
            log.info("Ud es cliente");

        if (request.isUserInRole("PASEADOR"))
            log.info("Ud es paseador");

        return "redirect:/home";
    }

    /**
     * Set user in session
     * @param request
     */
    private void setUserInSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        PersonaDetalleRequest persona = (PersonaDetalleRequest) session.getAttribute("loggedInUser");
        if(persona == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Personadetalle user = userService.buscarPorEmailOrUserName(auth.getName()).getIdpersona();
            PersonaDetalleRequest authUser = new PersonaDetalleRequest();
            authUser.setId(user.getId());
            authUser.setNombres(user.getNombres().toUpperCase());
            authUser.setApellidos(user.getApellidos().toUpperCase());
            authUser.setIdentificacion(user.getIdentificacion());
            authUser.setCelular(user.getCelular());
            authUser.setEmail(user.getEmail());
            this.setMenuItemsInSession(session, auth);
            session.setAttribute("loggedInUser", authUser);
        }
    }

    /**
     * Set menu items in session
     * @param session
     * @param auth
     */
    private void setMenuItemsInSession(HttpSession session, Authentication auth) {
        Set<String> roles = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
        Set<MenuItem> menuItems = menuService.getMenuByRoles(roles);
        session.setAttribute("menuItems", menuItems);
    }

    //RECUPERAR CONTRASEÑA
    @GetMapping("/recupera")
    public String mostrarFormularioRecuperacion() {
        return "/content-page/recuperaPass";  //return "/landing/login";
    }

    // Paso 1: Validar y enviar enlace de recuperación
    @PostMapping("/validamail")
    public String buscarMail(@RequestParam("email") String email, Model model) {
        Personadetalle persona = servicioPersonaDetalle.buscarEmail(email);

        if (persona == null || !persona.getEmail().equals(email)) {
            model.addAttribute("error", "El correo no existe, comuníquese con el administrador.");
            return "/content-page/recuperaPass";
        }

        // Generar token y enviarlo por correo
        String token = generarTokenRecuperacion();
       // Usuariodetalle usuario = usuarioDetalleService.buscarPorPersona(persona);
        Usuariodetalle usuario = userService.buscarPorId(persona.getId()); // Buscar el usuario asociado
        guardarToken(usuario, token);
        enviarCorreoRecuperacion(email, token);

        model.addAttribute("mensaje", "Se ha enviado un enlace de recuperación a su correo.");
        return "/content-page/recuperaPass";
    }

    // Paso 2: Mostrar formulario para restablecer la contraseña
    @GetMapping("/restablecer")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
        Usuariodetalle usuario = userService.buscarToken(token);

        if (usuario == null) {
            model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
            return "/content-page/restablecer";
        }

        model.addAttribute("token", token);  // Pasar el token al formulario
        return "/content-page/restablecer";
    }

    // Paso 3: Procesar el restablecimiento de contraseña
    @PostMapping("/restablecer")
    public String restablecerContrasena(@RequestParam("token") String token,
                                        @RequestParam("nuevaContrasena") String nuevaContrasena,
                                        Model model) {
        Usuariodetalle usuario = userService.buscarToken(token);

        if (usuario == null) {
            model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
            return "/content-page/restablecer";
        }

        // Encriptar y guardar la nueva contraseña
        usuario.setPassword(encriptarContrasena(nuevaContrasena));
        usuario.setToken(null);  // Eliminar el token después de usarlo
        userService.insertarUsuarioDetalle(usuario);

        model.addAttribute("mensaje", "Su contraseña ha sido restablecida con éxito.");
        return "/content-page/login";  // Redirigir a la página de inicio de sesión
    }

    // Métodos auxiliares
    private String generarTokenRecuperacion() {
        return UUID.randomUUID().toString();
    }

    private void guardarToken(Usuariodetalle usuario, String token) {
        usuario.setToken(token);
        userService.insertarUsuarioDetalle(usuario);
    }

    private void enviarCorreoRecuperacion(String email, String token) {
        String urlRecuperacion = "http://tusitio.com/restablecer?token=" + token;// cambiar por el url del dominio de la aplicacion
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText("Haga clic en el siguiente enlace para restablecer su contraseña: " + urlRecuperacion);
        mailSender.send(mensaje);
    }

    private String encriptarContrasena(String contrasena) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(contrasena);
        //passwordEncoder.encode(contrasena)
    }



}