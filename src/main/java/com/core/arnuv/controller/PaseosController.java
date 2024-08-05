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
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.utils.ArnuvUtils;

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

	@GetMapping("/listar")
	public String listar(Model model) {
		List<Paseo> listapaseos = paseoService.listarPaseos();
		model.addAttribute("lista", listapaseos);
		return "/content-page/paseo-listar";
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

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
