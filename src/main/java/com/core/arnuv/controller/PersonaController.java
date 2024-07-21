package com.core.arnuv.controller;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.utils.ArnuvNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/persona")
public class PersonaController {
	@Autowired
	private ICatalogoDetalleService servicioCatalogoDetalle;
	@Autowired
	private IPersonaDetalleService servicioPersonaDetalle;
	
	@GetMapping("crear")
	public String personCreate(Model model) {
		model.addAttribute("personDetRequest", new PersonaDetalleRequest());
		return "/admin/persona-crear";
	}

	@PostMapping("create-access")
	private String personCreateAccess(@ModelAttribute("personDetRequest") PersonaDetalleRequest persona) {
		//var catDetEntity = servicioCatalogoDetalle.buscarPorId(persona.getIdcatalogoidentificacion(), persona.getIddetalleidentificacion());
		Personadetalle personadetalle = persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion", "iddetalleidentificacion");
		//personadetalle.setCatalogodetalle(catDetEntity);
		Personadetalle personaEntity;
		try {
			personaEntity = servicioPersonaDetalle.insertarPersonaDetalle(personadetalle);
		} catch (DataIntegrityViolationException e) {
			throw new ArnuvNotFoundException("Error al guardar datos: {0}", e.getMessage().split("Detail:")[1].split("]")[0]);
		}
		return "redirect:/usuario/crear/".concat(personaEntity.getId().toString());
	}
}