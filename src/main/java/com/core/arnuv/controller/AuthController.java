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
    public String loginAccess(@ModelAttribute("loginRequest") LoginRequest loginRequest) {
        var entity = serviceUsuarioDetalle.buscarPorEmail(loginRequest.getEmail());
        LoginResponse resp = new LoginResponse();
        if (entity == null) {
            throw new ArnuvNotFoundException("El usuario {0} no existe", loginRequest.getEmail());
        } else {
            if (!entity.getPassword().equals(loginRequest.getPassword())) {
                throw new ArnuvNotFoundException("Credenciales del usuario no existe");
            }
            if (!entity.getEstado()) {
                throw new ArnuvNotFoundException("El usuario esta deshabilitado");
            }
        }
        log.info("Login exitoso");
        return "redirect:/usuario/listar";
    }
}