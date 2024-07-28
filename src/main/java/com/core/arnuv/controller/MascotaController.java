package com.core.arnuv.controller;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.core.arnuv.model.CatalogoDetalle;
import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.service.ICatalogoDetalleService;
import com.core.arnuv.service.IMascotaDetalleService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mascota")
@Slf4j
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
		List<MascotaDetalle> listaMascotas = mscotaDetalleService.listarMascotasDetalle();
		model.addAttribute("lista", listaMascotas);
		return "/admin/mascotas-listar";
	}

	@GetMapping("/nuevo")
	public String crear(Model model) {
		model.addAttribute("nuevo", new MascotaDetalle());	 
		model.addAttribute("catalogo",catalogoDetalleService.listarCatalogoDetalle());
		return "/admin/mascotas-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") MascotaDetalle nuevo, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
			MascotaDetalle mascota = nuevo;
			mascota.setPhotoPet(file);
            mscotaDetalleService.insertarMascotaDetalle(nuevo);
			return "redirect:/mascota/listar";
        } catch (IOException e) {
			redirectAttributes.addFlashAttribute("message", "La imagen no se pudo guardar");
			return "/admin/mascotas-crear";
        }
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
