package com.core.arnuv.controller;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.utils.ArnuvNotFoundException;
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
@RequestMapping("/persona")
public class PersonaController {
	@Autowired
	private ICatalogoDetalleService servicioCatalogoDetalle;
	@Autowired
	private IPersonaDetalleService servicioPersonaDetalle;
	
	@Autowired
	private IUbicacionService  ubicacionService;
	
	@GetMapping("crear")
	public String personCreate(Model model) {
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		return "/content-page/persona-crear";
	}

	@PostMapping("create-access")
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
			ubicacion.setIdpersona(personaEntity);
			ubicacionService.insertarUbicacion(ubicacion);
			return "redirect:/usuario/crear/".concat(personaEntity.getId().toString());
		} catch (DataIntegrityViolationException e) {
			//throw new ArnuvNotFoundException("Error al guardar datos: {0}", e.getMessage().split("Detail:")[1].split("]")[0]);

			//String errorMessage = "Error al guardar datos: Ya existe la llave (celular)=" + persona.getCelular();
			//System.out.println("llega este error: " + errorMessage);
			//model.addAttribute("error", errorMessage);
			String errorMessage;
			// Detectar el campo específico en el mensaje de error
			/*if (e.getMessage().contains("uk_eqrqigy92n8fi43p0e9pmf9aw")) { // Email
				errorMessage = "Error al guardar datos: Ya existe el email registrado=" + persona.getEmail();
			} else if (e.getMessage().contains("uk_q5r1m95xoe8hnuv378tdsymul")) { // Celular
				errorMessage = "Error al guardar datos: Ya existe el celular registrado=" + persona.getCelular();
			} else if (e.getMessage().contains("uk_xxxxxxx")) { // Identificación (ajustar según el índice)
				errorMessage = "Error al guardar datos: Ya existe la identificacion registrada=" + persona.getIdentificacion();
			} else {
				errorMessage = "Error al guardar datos:" + e.getMessage();
			}
			model.addAttribute("error", errorMessage);
			model.addAttribute("nuevo", persona); */// Para mantener los datos del formulario

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

// Agregar mensaje de error al modelo
			model.addAttribute("error", errorMessage);
			model.addAttribute("nuevo", persona);

			return "/content-page/persona-crear"; // La misma página del formulario


		}
		//return "redirect:/usuario/crear/".concat(personaEntity.getId().toString());
	}
}