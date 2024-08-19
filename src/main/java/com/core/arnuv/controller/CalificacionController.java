package com.core.arnuv.controller;

import static com.core.arnuv.constants.Constants.KEY_LINK_MAPA_GOOGLE;
import static com.core.arnuv.constants.Constants.KEY_PLANTILLA_MAIL;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.core.arnuv.model.Calificacion;
import com.core.arnuv.model.Parametros;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.service.ICalificacionService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.utils.ArnuvUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/calificacion")
public class CalificacionController {

	@Autowired
	private ICalificacionService calificacionService;
	@Autowired
	private IPaseoService paseoService;

	@Autowired
	private IPersonaDetalleService personaDetalleService;
	
	@Autowired 
	private ArnuvUtils arnuvUtils;
	
	@GetMapping("/listar")
	public String listar(Model model, HttpServletRequest request) {
		var idusuariologueado =arnuvUtils.getLoggedInUsername();
		List<Calificacion> listaCalificacion = calificacionService.BuscarPersonaPasedor(idusuariologueado.getId());
		model.addAttribute("lista", listaCalificacion);
		return "content-page/calificacion-listar";
		

	}
	
	@GetMapping("/nuevo/{paseoId}")
	public String crear(Model model, @PathVariable(value = "paseoId") int paseoId) {	

		System.out.print(paseoId);
		Calificacion calificacion = new Calificacion();
		calificacion.setPaseoID(paseoId);
		model.addAttribute("nuevo", calificacion);		
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());		
		return "content-page/calificacion-crear";
	}
	
	@PostMapping("/insertar")
	public String insertarCalificacion(@ModelAttribute("nuevaCalificacion") Calificacion nuevo)  {
		nuevo.setFecha(new Date());
		
		Paseo paseo = paseoService.buscarPorId(nuevo.getPaseoID());
		nuevo.setIdpersonapasedor(paseo.getIdpersonapasedor());
		nuevo.setIdpersonacliente(paseo.getIdpersonacliente());
		
		calificacionService.insertarCalificacion(nuevo);
		return "redirect:/paseo/listar";

	}
}
