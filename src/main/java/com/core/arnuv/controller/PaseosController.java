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
		return "/content/prueba";
	}

	@GetMapping("/nuevo")
	public String crear(Model model) {
		
		
		var listaUbi = ubicacionService.listarUbicacion();
		for (Ubicacion punto : listaUbi) {
			System.out.println("----------------");
			System.out.println(punto.getLatitud());
		}
		double miLatitud = -34.6037; // Tu latitud
		double miLongitud = -58.3816; // Tu longitud
		double radio = parametroService.getParametro("RADIO").getValorNumber();
		
		List<Ubicacion> ubiPaseadores = new ArrayList<>();
		
		for (Ubicacion usuario : ubiPaseadores) {
			double distancia = ArnuvUtils.distance(miLatitud, miLongitud, Double.parseDouble(usuario.getLatitud()), Double.parseDouble(usuario.getLongitud()));
			if (distancia <= radio) {
				ubiPaseadores.add(usuario);
			}
		}
		
		for (Ubicacion punto : ubiPaseadores) {
			System.out.println("puntos ----------------");
			System.out.println(punto.getLatitud());
		}
		
		// var personaUbi = ubicacionService.buscarPorId(2);
/*
		int idpersona = 9;
		Ubicacion UbiPersona = listaUbi.stream()
				.filter(p -> p.getIdpersona().equals(idpersona))
				.findFirst().get();
		
		var perLatitud = Double.parseDouble( UbiPersona.getLatitud());
		var perLongitud =  Double.parseDouble(UbiPersona.getLongitud());
		
		double radio = parametroService.getParametro("RADIO").getValorNumber();
		List<Ubicacion> ubiPaseadores = new ArrayList<>(); 
		for (Ubicacion punto : listaUbi) {
			var latitud =  Double.parseDouble(punto.getLatitud());
			var longitud =Double.parseDouble(punto.getLongitud());
			double distancia = ArnuvUtils.distance(perLatitud ,perLongitud , latitud, longitud);
			//double distancia = ArnuvUtils.distance(miLatitud, miLongitud, usuario.getLatitud(), usuario.getLongitud());
			if (distancia <= radio) {
				ubiPaseadores.add(punto);
			}
		}
		//ArnuvUtils.distance(UbiPersona.getLatitud(), UbiPersona.getLongitud(), latitud, 0);
		for (Ubicacion punto : ubiPaseadores) {
			System.out.println("----------------");
			System.out.println(punto.getLatitud());
		}
		*/
		model.addAttribute("nuevo", new Paseo());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());

		//model.addAttribute("ubicacion", ubiPaseadores);
		return "/content/paseo-crear";
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
		return "/content/paseo-crear";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
