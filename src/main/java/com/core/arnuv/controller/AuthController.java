package com.core.arnuv.controller;

import com.core.arnuv.model.*;
import com.core.arnuv.request.ChangePasswordRequest;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.request.UsuarioRolRequest;
import com.core.arnuv.service.*;
import com.core.arnuv.services.imp.EmailSender;
import com.core.arnuv.utils.ArnuvNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.core.arnuv.constants.Constants.KEY_PLANTILLA_MAIL;
import static com.core.arnuv.constants.Constants.KEY_LINK_MAPA_GOOGLE;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final IUsuarioDetalleService userService;
    private final IMenuService menuService;
    private final IPersonaDetalleService servicioPersonaDetalle;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
	private final IUbicacionService  ubicacionService;
    private final IRolService servicioRol;
    private final IUsuarioRolService servicioUsuarioRol;
    private final IParametroService parametroService;
    

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
        return "landing/login";
    }
    /*-----------------------CREAR NUEVO PARAMATROS ------------------------*/
    @GetMapping("/test")
   	public String tsest(Model model) {   		
    	
   		return "landing/plantillamail";
   	}
    
    /*-----------------------CREAR NUEVO CLIENTE ------------------------*/
    @GetMapping("/crearCliente")
	public String personCliente(Model model) {
    	Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);
		return "landing/persona-crearCliente";
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

			return "landing/persona-crearCliente";
		}
	}
    
    @GetMapping("/crearUsuarioCliente/{personaId}")
    public String personCreate(Model model, @PathVariable("personaId") Integer personaId) {
        UsuarioDetalleRequest requestUser = new UsuarioDetalleRequest();
        requestUser.setIdpersona(personaId);
        model.addAttribute("nuevo", requestUser);
        return "landing/usuario-crearCliente";
    }
    @PostMapping("/create-accessUsuarioCliente")
    private String personCreateAccess(@ModelAttribute("nuevo") UsuarioDetalleRequest usuario) throws UnsupportedEncodingException, MessagingException {
        var personaentity = servicioPersonaDetalle.buscarPorId(usuario.getIdpersona());
        Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
        usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuariodetalle.setIdpersona(personaentity);
        usuariodetalle.setEstado(true);
        Usuariodetalle entity = null;
        try {
            entity = userService.insertarUsuarioDetalle(usuariodetalle);
            
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
            
            String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(), StandardCharsets.UTF_8);
            String mensajeDinamico = "BIENVENIDO A LA FUNDACION ARNUV! <br> "+personaentity.getNombres()+ " "+personaentity.getApellidos();
            htmlContent = htmlContent.replace("{{mensajeBienvenida}}", "<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">" + mensajeDinamico.toUpperCase() + "</span></p>");
            
            emailSender.sendEmail(personaentity.getEmail(), "CREACIÓN DE USUARIO", htmlContent);
            
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

    //RECUPERAR CONTRASEÑA
    @GetMapping("/recupera")
    public String mostrarFormularioRecuperacion() {
        return "landing/recuperaPass";
    }

    // Paso 1: Validar y enviar enlace de recuperación
    @PostMapping("/validamail")
    public String buscarMail(@RequestParam("email") String email, Model model)
            throws MessagingException, UnsupportedEncodingException {
        // Generar token y enviarlo por correo
        String token = generarTokenRecuperacion();
        Usuariodetalle usuario = userService.buscarPorEmailOrUserName(email);
        if (usuario == null) {
            model.addAttribute("error", "El correo electronico no se encuentra registrado.");
            return "landing/recuperaPass";
        }
        guardarToken(usuario, token);
        enviarCorreoRecuperacion(email, token);
        
        
        
        model.addAttribute("mensaje", "Se ha enviado un enlace de recuperación a su correo.");
        return "landing/index";
    }

    // Paso 2: Mostrar formulario para restablecer la contraseña
    @GetMapping("/restablecer")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
        Usuariodetalle usuario = userService.buscarToken(token);
        if (usuario == null) {
            if(usuario.getToken() == null && isTokenValid(usuario.getToken())){
                model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
                return "landing/recuperaPass";
            }
        }
        ChangePasswordRequest changePasswordReq = new ChangePasswordRequest();
        changePasswordReq.setUser(usuario);
        model.addAttribute("changePass", changePasswordReq);
        return "landing/restablecer";
    }

    // Paso 3: Procesar el restablecimiento de contraseña
    @PostMapping("/cambiarPass")
    public String restablecerContrasena(@ModelAttribute("changePass") ChangePasswordRequest changePasswordReq,
                                        Model model) throws UnsupportedEncodingException, MessagingException {
        Usuariodetalle usuario = changePasswordReq.getUser();
        // Encriptar y guardar la nueva contraseña
        usuario.setPassword(encriptarContrasena(changePasswordReq.getPassword()));
        userService.insertarUsuarioDetalle(usuario);
        String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(), StandardCharsets.UTF_8);
        String mensajeDinamico = "BIENVENIDO A LA FUNDACION ARNUV! <br>TU CAMBIO DE CONTRASEÑA FUE EXITOSA <br>"+usuario.getIdpersona().getNombres()+ " "+usuario.getIdpersona().getApellidos();
        htmlContent = htmlContent.replace("{{mensajeBienvenida}}", "<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">" + mensajeDinamico.toUpperCase() + "</span></p>");
        emailSender.sendEmail(usuario.getIdpersona().getEmail(), "CAMBIO DE CONTRASEÑA", htmlContent);
        model.addAttribute("mensaje", "Su contraseña ha sido restablecida con éxito.");
        return "landing/login";  // Redirigir a la página de inicio de sesión
    }

    // Métodos auxiliares
    private String generarTokenRecuperacion() {
        return UUID.randomUUID().toString();
    }

    private void guardarToken(Usuariodetalle usuario, String token) {
        Instant now = Instant.now();
        // Sumar 5 minutos a la hora actual
        Instant expirationTime = now.plusSeconds(300);
        Token nuevoToken = new Token();
        nuevoToken.setToken(token);
        nuevoToken.setStartDate(now);
        nuevoToken.setEndDate(expirationTime);
        nuevoToken.setEstado(Boolean.TRUE);
        usuario.setToken(nuevoToken);
        userService.insertarUsuarioDetalle(usuario);
    }

    public boolean isTokenValid(Token token) {
        // Obtén la hora actual
        Instant now = Instant.now();

        // Verifica si la hora actual está antes de la fecha de expiración del token
        return now.isBefore(token.getEndDate());
    }

    private void enviarCorreoRecuperacion(String email, String token)
            throws MessagingException, UnsupportedEncodingException {
        String urlRecuperacion = "http://127.0.0.1:8087/auth/restablecer?token=" + token;
        
        String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(), StandardCharsets.UTF_8);
        String mensajeDinamico = "BIENVENIDO A LA FUNDACION ARNUV! <br> CAMBIA TU CONTRASEÑA EN EL SIGUIENTE ENLACE: <br>"+urlRecuperacion;
        htmlContent = htmlContent.replace("{{mensajeBienvenida}}", "<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">" + mensajeDinamico + "</span></p>");
        
        //emailSender.sendEmail('dsfsdf', "Creacion de usuario", htmlContent);
        emailSender.sendEmail(email, "RESTABLECER CONTRASEÑA", htmlContent);
        
        //emailSender.sendEmail(email, "Recuperación de contraseña", "Haga clic en el siguiente enlace para restablecer su contraseña: " + urlRecuperacion);
    }

    private String encriptarContrasena(String contrasena) {
        return passwordEncoder.encode(contrasena);
    }
}