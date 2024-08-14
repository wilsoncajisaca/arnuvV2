package com.core.arnuv.controller;

import com.core.arnuv.model.MenuItem;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Rol;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.model.UsuariorolId;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.request.UsuarioRolRequest;
import com.core.arnuv.service.IMenuService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IRolService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.service.IUsuarioDetalleService;
import com.core.arnuv.service.IUsuarioRolService;
import com.core.arnuv.utils.ArnuvNotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final IUsuarioDetalleService userService;
    private final IMenuService menuService;
    
	private final IPersonaDetalleService servicioPersonaDetalle;
	private final IUbicacionService  ubicacionService;
    private final PasswordEncoder passwordEncoder;	
    private final IUsuarioDetalleService usuarioDetalleService;    
    private final IRolService servicioRol;
    private final IUsuarioRolService servicioUsuarioRol;
    private final IUsuarioDetalleService servicioUsuarioDetalle;

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
    
    /*-----------------------CREAR NUEVO CLIENTE ------------------------*/
    @GetMapping("/crearCliente")
	public String personCliente(Model model) {
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		return "/landing/persona-crearCliente";
	}
    
    @PostMapping("/create-access")
	private String personCreateAccess(@ModelAttribute("nuevo") PersonaDetalleRequest persona, Model model) {
		//var catDetEntity = servicioCatalogoDetalle.buscarPorId(persona.getIdcatalogoidentificacion(), persona.getIddetalleidentificacion());
		Personadetalle personadetalle = persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion", "iddetalleidentificacion");
		//personadetalle.setCatalogodetalle(catDetEntity);
		Personadetalle personaEntity;
		try {
			personaEntity = servicioPersonaDetalle.insertarPersonaDetalle(personadetalle);
			var ubicacion = new Ubicacion();
			ubicacion.setLatitud(persona.getLatitud());
			ubicacion.setLongitud(persona.getLongitud());
			ubicacion.setIsDefault(1);
			ubicacion.setIdpersona(personaEntity);
			ubicacionService.insertarUbicacion(ubicacion);
			return "redirect:/auth/crearUsuarioCliente/".concat(personaEntity.getId().toString());
		} catch (DataIntegrityViolationException e) {
			String errorMessage;
			if (e.getMessage().contains("uk_eqrqigy92n8fi43p0e9pmf9aw")) { // Email
				errorMessage = "Error al guardar datos: Ya existe el email registrado=" + persona.getEmail();
			} else if (e.getMessage().contains("uk_q5r1m95xoe8hnuv378tdsymul")) { // Celular
				errorMessage = "Error al guardar datos: Ya existe el celular registrado=" + persona.getCelular();
			} else if (e.getMessage().contains("uk_jmjk4q6y2fnm48qlml12e5cl9")) { // Identificación
				errorMessage = "Error al guardar datos: Ya existe la identificacion registrada=" + persona.getIdentificacion();
			} else {
				// Mensaje genérico si no se detecta un campo específico
				errorMessage = "Error al guardar datos: Se ha detectado un problema con los datos ingresados.";
			}


			model.addAttribute("error", errorMessage);
			model.addAttribute("nuevo", persona);

			return "/landing/persona-crearCliente";


		}
		
	}
    
    @GetMapping("/crearUsuarioCliente/{personaId}")
    public String personCreate(Model model, @PathVariable("personaId") Integer personaId) {
        UsuarioDetalleRequest requestUser = new UsuarioDetalleRequest();
        requestUser.setIdpersona(personaId);
        model.addAttribute("nuevo", requestUser);
        return "/landing/usuario-crearCliente";
    }
    @PostMapping("/create-accessUsuarioCliente")
    private String personCreateAccess(@ModelAttribute("nuevo") UsuarioDetalleRequest usuario) {
        var personaentity = servicioPersonaDetalle.buscarPorId(usuario.getIdpersona());
        Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
        usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuariodetalle.setIdpersona(personaentity);
        usuariodetalle.setEstado(true);
        Usuariodetalle entity = null;
        try {
            entity = usuarioDetalleService.insertarUsuarioDetalle(usuariodetalle);
            
            ///////////////////////////////////////////////
            var rolCliente = servicioRol.findByNombre("ROLE_CLIENTE");            
            var rolentity = servicioRol.buscarPorId(rolCliente.getId());            
            
            
            UsuariorolId usuariorolId = new UsuariorolId();
            UsuarioRolRequest nuevo1 = new UsuarioRolRequest();
            
            usuariorolId.setIdusuario(entity.getIdusuario());
            usuariorolId.setIdrol(rolCliente.getId());
            
            
            var usuariorolentity = nuevo1.mapearDato(nuevo1, Usuariorol.class, "idrol","idusuario");
            usuariorolentity.setIdusuario(entity);
            usuariorolentity.setIdrol(rolentity);
            usuariorolentity.setId(usuariorolId);
            
            servicioUsuarioRol.insertarUsuarioRol(usuariorolentity);
            
            
        } catch (DataIntegrityViolationException e) {
            throw new ArnuvNotFoundException("Error al guardar datos: {0}", e.getMessage().split("Detail:")[1].split("]")[0]);
        }
        
        return "redirect:/index";
    }   
    
    /*-----------------------CREAR NUEVO CLIENTE ------------------------*/

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
}