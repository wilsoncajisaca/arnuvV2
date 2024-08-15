package com.core.arnuv.controller;

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

import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.BusquedaFechasResponse.BusquedaFechaeDto;
import com.core.arnuv.response.UbicacionDetalleResponse;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.utils.ArnuvUtils;
import java.time.LocalDate;
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
      
		
		
       // model.addAttribute("lista", listapaseos1);
        return "/content-page/paseo-listar-BucarPorFechas"; // Cambia esto por el nombre de la vista Thymeleaf que estás usando
    }

	@GetMapping("/nuevo")
	public String crear(Model model) {		

		var idusuariologueado =arnuvUtils.getLoggedInUsername();
		model.addAttribute("nuevo", new Paseo());
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.findByIdpersonaId(idusuariologueado.getId()));
		model.addAttribute("ubicaciones",ubicacionService.listarUbicacion());
		return "/content-page/paseo-crear";
	}

	// guardar
	@PostMapping("/insertar")
	public String guardar(@ModelAttribute("nuevo") Paseo nuevo, HttpServletRequest request) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String formattedDate = formatter.format(date);		
		
		if (request.isUserInRole("CLIENTE")) {
			Personadetalle personaCLiente = new Personadetalle();
			
			var idusuariologueado =arnuvUtils.getLoggedInUsername().getId();
			personaCLiente.setId(idusuariologueado);
			nuevo.setIdpersonacliente(personaCLiente);
			
        }
		/*
		if (nuevo.getId() != 0){
			Paseo existePaseo = paseoService.buscarPorId(nuevo.getId());		
			nuevo.setFecha(existePaseo.getFecha());
		}
		*/
			
		
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
		
		Ubicacion ubicacionCliente = ubicacionService.ubicacionPersonaPorDefecto(itemrecuperado.getIdpersonacliente().getId());
		List <Ubicacion> listUbicacionCliente = new ArrayList<>();;		
		listUbicacionCliente.add(ubicacionCliente);
		
		
		
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		model.addAttribute("ubicacion", listUbicacionCliente);
		return "/content-page/paseoPaseador-crear";
	}
	
	@GetMapping("/editarCliente/{idpaseo}")
	public String editarCliente(@PathVariable(value = "idpaseo") int codigo, Model model) {
		Paseo itemrecuperado = paseoService.buscarPorId(codigo);
		model.addAttribute("nuevo", itemrecuperado);
		model.addAttribute("persona", personaDetalleService.listarTodosPersonaDetalle());
		model.addAttribute("tarifario", ITarifarioService.listarTarifarios());
		model.addAttribute("mascota", mascotaDetalleService.listarMascotasDetalle());
		return "/content-page/paseoCliente-ver";
	}

	// eliminar
	@GetMapping("/eliminar/{codigo}")
	public String eliminar(@PathVariable(value = "codigo") int codigo, Model model) {
		paseoService.eliminarPaseo(codigo);
		return "redirect:/paseo/listar";
	}
}
