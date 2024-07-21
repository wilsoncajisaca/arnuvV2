package com.core.arnuv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.core.arnuv.model.CatalogoDetalle;
import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IMascotaDetalleService;

@Controller
@RequestMapping("/mascota")
public class MascotaController {

	
	@Autowired
	public IMascotaDetalleService mscotaDetalleService;
	
	@Autowired
	public ICatalogoDetalleService catalogoDetalleService;

	/*
	@GetMapping("/listar")
	public String login() {
		return "/admin/mascotas";
	}
*/
	@GetMapping("/listar")
	public String listarColores(Model model) {
		List<MascotaDetalle> listaMascotas = mscotaDetalleService.listarMascotasDetalle(); // recuperando informacion de la BD
		model.addAttribute("lista", listaMascotas);
		return "/admin/listarmascotas"; // ruta fìsca de la pagina
	}

	@GetMapping("/nuevo") // url de llamada mapeo de url
	public String crear(Model model) {
		model.addAttribute("nuevo", new MascotaDetalle());	 
		model.addAttribute("catalogo",catalogoDetalleService.listarCatalogoDetalle());
		return "/admin/mascotas"; // ruta fìsca de la pagina
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") MascotaDetalle nuevo) {
		//System.out.println("aqui javi " + nuevoColor.getEstado());
		mscotaDetalleService.insertarMascotaDetalle(nuevo);
		return "redirect:/mascota/listar";

	}

	// editar
	
	@GetMapping("/editar/{idmascota}")
	public String editarCurso(@PathVariable(value = "idmascota") int codigo, Model model) {
		MascotaDetalle itemrecuperado = mscotaDetalleService.buscarMascotaID(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("catalogos", catalogoDetalleService.listarCatalogoDetalle());
		return "/admin/mascotas";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminarColor(@PathVariable(value = "codigo") int codigo, Model model) {
		mscotaDetalleService.EliminarMascotaDetalle(codigo);
		return "redirect:/mascota/listar";
	}
	
}
