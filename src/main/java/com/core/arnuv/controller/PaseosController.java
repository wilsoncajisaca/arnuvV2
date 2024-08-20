package com.core.arnuv.controller;

import static com.core.arnuv.constants.Constants.KEY_LINK_MAPA_GOOGLE;
import static com.core.arnuv.constants.Constants.ESTADO_APROBADO;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.core.arnuv.model.Calificacion;
import com.core.arnuv.model.Parametros;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.BusquedaFechasResponse.BusquedaFechaeDto;
import com.core.arnuv.service.ICalificacionService;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.services.imp.EmailSender;
import com.core.arnuv.utils.ArnuvUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import static com.core.arnuv.constants.Constants.KEY_PLANTILLA_MAIL;

@Controller
@RequestMapping("/paseo")
public class PaseosController {

	@Autowired
	private IPaseoService paseoService;

	@Autowired
	private IPersonaDetalleService personaDetalleService;

	@Autowired
	private ITarifarioService ITarifarioService;

	@Autowired
	private IMascotaDetalleService mascotaDetalleService;

	@Autowired
	private IUbicacionService ubicacionService;
	
	@Autowired
	private IParametroService parametroService;
	@Autowired 
	private ArnuvUtils arnuvUtils;
	@Autowired 
	private EmailSender emailSender;
	
	@Autowired
	private ICalificacionService calificacionService;
	
	@GetMapping("/listar")
	public String listar(Model model, HttpServletRequest request) {
		var idusuariologueado =arnuvUtils.getLoggedInUsername();
		
		if (request.isUserInRole("ADMIN")) {
			List<Paseo> listapaseos = paseoService.listarPaseos();		
			model.addAttribute("lista", listapaseos);
			return "/content-page/paseo-listar";
		}
        if (request.isUserInRole("CLIENTE")) {
        	List<Paseo> listapaseos = paseoService.buscarpersonacliente(idusuariologueado.getId());
    		model.addAttribute("lista", listapaseos);
    		return "/content-page/paseo-listar";
        }

        if (request.isUserInRole("PASEADOR")) {
        	List<Paseo> listapaseos = paseoService.buscaridpersonapasedor(idusuariologueado.getId());
    		model.addAttribute("lista", listapaseos);
    		return "/content-page/paseoPaseador-listar";
        }

        return "redirect:/home";
	
		

	}
	
	@GetMapping("/buscarPorFecha")
    public String buscarPorFecha( Model model) 
    
	{		
		BusquedaFechaeDto busquedaFechasResponse= new BusquedaFechaeDto();
        model.addAttribute("nuevo", busquedaFechasResponse);
        return "/content-page/paseo-listar-BucarPorFechas"; // Cambia esto por el nombre de la vista Thymeleaf que estás usando
    }
	
	@GetMapping("/ListarbuscarPorFecha")
    public String ListarbuscarPorFecha( 
    		@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model,HttpServletRequest request)
	{		

	
        Date fechaIni = Date.from(fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFinal = Date.from(fechaFin.atStartOfDay(ZoneId.systemDefault()).toInstant());
        var idusuariologueado =arnuvUtils.getLoggedInUsername();
        
        if (request.isUserInRole("CLIENTE")) {
        	List<Paseo> listapaseos = paseoService.buscarRangoFechasCliente(fechaIni, fechaFinal, idusuariologueado.getId());
    		model.addAttribute("lista", listapaseos);
    		return "/content-page/paseo-listar-BucarPorFechas";
        }

        if (request.isUserInRole("PASEADOR")) {
        	List<Paseo> listapaseos = paseoService.buscarRangoFechasPaseador(fechaIni, fechaFinal, idusuariologueado.getId());
    		model.addAttribute("lista", listapaseos);
    		return "/content-page/paseo-listar-BucarPorFechas";
        }
        
        return "/content-page/paseo-listar-BucarPorFechas"; // Cambia esto por el nombre de la vista Thymeleaf que estás usando
    }

	@GetMapping("/nuevo")
	public String crear(Model model) {		

		var idusuariologueado =arnuvUtils.getLoggedInUsername();
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		model.addAttribute("nuevo", new Paseo());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.findByIdpersonaId(idusuariologueado.getId()));
		model.addAttribute("ubicaciones",ubicacionService.listarUbicacion());
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);
		return "/content-page/paseo-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") Paseo nuevo, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String formattedDate = formatter.format(date);		
		
		
		if (request.isUserInRole("CLIENTE")) {
			Personadetalle personaCLiente = new Personadetalle();
			
			var idusuariologueado =arnuvUtils.getLoggedInUsername().getId();
			personaCLiente.setId(idusuariologueado);
			nuevo.setIdpersonacliente(personaCLiente);
			
        }
		
		if (nuevo.getId() == -1){
			
			Personadetalle personadetalle = personaDetalleService.buscarPorId(nuevo.getIdpersonapasedor().getId());
			String htmlContent = new String(parametroService.getParametro(KEY_PLANTILLA_MAIL).getArchivos(), StandardCharsets.UTF_8);
            String mensajeDinamico = "SOLICITUD DE SERVICIO DE PASEO A MASCOTA ! <br> !! REVISA TU BANDEJA DE PASEOS !!";
            htmlContent = htmlContent.replace("{{mensajeBienvenida}}", "<p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span style=\"font-family: Lato, sans-serif; font-size: 16px; line-height: 22.4px;\">" + mensajeDinamico.toUpperCase() + "</span></p>");
            System.out.println(personadetalle.getEmail());
            emailSender.sendEmail(personadetalle.getEmail(), "SOLICITUD DE SERVICIO", htmlContent);
		}
		
			
		
		paseoService.insertarPaseo(nuevo);
		return "redirect:/paseo/listar";

	}
	
	

	@GetMapping("/editar/{idpaseo}")
	public String editar(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);
		return "/content-page/paseo-crear";
	}
	
	@GetMapping("/editarPaseador/{idpaseo}")
	public String editarPaseador(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		
		Ubicacion ubicacionCliente = ubicacionService.ubicacionPersonaPorDefecto(itemrecuperado.getIdpersonacliente().getId());
		List <Ubicacion> listUbicacionCliente = new ArrayList<>();;		
		listUbicacionCliente.add(ubicacionCliente);
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);		
		
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		model.addAttribute("ubicacion", listUbicacionCliente);
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);
		return "/content-page/paseoPaseador-crear";
		
		//@{/calificacion/nuevo/{paseoId}(paseoId=${paseoID})}
		
	}
	
	@GetMapping("/editarCliente/{idpaseo}")
	public String editarCliente(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		Parametros linkMapaGoogle = parametroService.getParametro(KEY_LINK_MAPA_GOOGLE);
		Calificacion calificacion =  calificacionService.findByIdpaseoId(codigo);
		String errorMessag="";
		if(calificacion == null && itemrecuperado.getEstado().equals(ESTADO_APROBADO)) {
			errorMessag ="Tienes que calificar el paseo antes de finalizar";
			
		}
		
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("paseoID", itemrecuperado.getId());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		model.addAttribute("error", errorMessag);			
		model.addAttribute("linkMapaGoogle", linkMapaGoogle);
		return "/content-page/paseoCliente-ver";
	}
	
	@PostMapping("/finalizarPaseo")
	public String finalizarPaseo(@ModelAttribute("nuevo") Paseo nuevo, HttpServletRequest request,Model model) {
		
		Calificacion calificacion =  calificacionService.findByIdpaseoId(nuevo.getId());
		
		if(calificacion == null) {
			String errorMessage ="Tienes que calificar el paseo antes de finalizar";
			model.addAttribute("error", errorMessage);			
			return "redirect:/paseo/editarCliente/".concat(String.valueOf(nuevo.getId()));
		}
		else {
			
			paseoService.insertarPaseo(nuevo);
			
			return "redirect:/paseo/listar";
			
		}
		
		

	}
	
	

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
