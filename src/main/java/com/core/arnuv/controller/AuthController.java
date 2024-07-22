package com.core.arnuv.controller;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.request.LoginRequest;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.response.LoginResponse;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.utils.ArnuvNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    private IUsuarioDetalleService serviceUsuarioDetalle;
    @Autowired
    private ICatalogoDetalleService servicioCatalogoDetalle;
    @Autowired
    private IPersonaDetalleService servicioPersonaDetalle;
    @Autowired
    private IUsuarioDetalleService servicioUsuarioDetalle;
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "/login";
    }

    /**
     * @param loginRequest
     * @return
     */
    @PostMapping("/login-access")
    public String loginAccess(@ModelAttribute("loginRequest") LoginRequest loginRequest, RedirectAttributes redirectAttributes) {
        var entity = serviceUsuarioDetalle.buscarPorEmail(loginRequest.getEmail());
        LoginResponse resp = new LoginResponse();
        if (entity == null) {
            redirectAttributes.addFlashAttribute("message", "El usuario "+ loginRequest.getEmail() +" no existe");
            return "redirect:/auth/login";
        } else {
            if (!entity.getPassword().equals(loginRequest.getPassword())) {
                redirectAttributes.addFlashAttribute("message", "Usuario y/o contraseña no son validos");
                return "redirect:/auth/login";
            }
            if (!entity.getEstado()) {
                redirectAttributes.addFlashAttribute("message", "El usuario esta deshabilitado");
                return "redirect:/auth/login";
            }
        }
        log.info("Login exitoso");
        return "redirect:/usuario/listar";
    }
}