package com.core.arnuv.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.utils.ArnuvUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/paseo")
public class PaseosController {

	@Autowired
	public IPaseoService paseoService;

	@Autowired
	public IPersonaDetalleService personaDetalleService;

	@Autowired
	public ITarifarioService ITarifarioService;

	@Autowired
	public IMascotaDetalleService mascotaDetalleService;

	@Autowired
	public IUbicacionService ubicacionService;
	
	@Autowired
	public IParametroService parametroService;
	
	@Autowired 
	public ArnuvUtils arnuvUtils;
	
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

	@GetMapping("/nuevo")
	public String crear(Model model) {
		
		model.addAttribute("nuevo", new Paseo());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		return "/content-page/paseo-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") Paseo nuevo) {
		
		Personadetalle personaCLiente = new Personadetalle();
		
		var idusuariologueado =arnuvUtils.getLoggedInUsername().getId();
		personaCLiente.setId(idusuariologueado);
		nuevo.setIdpersonacliente(personaCLiente);
		paseoService.insertarPaseo(nuevo);
		return "redirect:/paseo/listar";

	}

	@GetMapping("/editar/{idpaseo}")
	public String editar(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		return "/content-page/paseo-crear";
	}
	
	@GetMapping("/editarPaseador/{idpaseo}")
	public String editarPaseador(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		return "/content-page/paseoPaseador-crear";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
