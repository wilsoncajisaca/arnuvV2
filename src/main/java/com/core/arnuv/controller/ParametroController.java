package com.core.arnuv.controller;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.model.Parametros;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.response.BusquedaFechasResponse.BusquedaFechaeDto;
import com.core.arnuv.service.IParametroService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("parametro")
public class ParametroController {
	
	private final IParametroService  parametroService;
	
	
	@GetMapping("/nuevo")
	@PreAuthorize("hasRole('ADMIN')")
	public String crear(Model model) {
		model.addAttribute("nuevo", new Parametros());	 
		return "/content-page/plantillaSubirImagenes";
	}
	
	// guardar
		@PostMapping("/insertar")
		@PreAuthorize("hasRole('ADMIN')")
		public String guardar(@ModelAttribute("nuevo") Parametros nuevo, @RequestParam("file") MultipartFile file,
							  RedirectAttributes redirectAttributes) {
	        try {
				
				
	        	Parametros parametros = nuevo;	        	
	        	parametros.setFile(file);
				
				
	        	parametroService.save(nuevo);
	            
				return "redirect:/home";
	        } catch (IOException e) {
				redirectAttributes.addFlashAttribute("message", "La imagen no se pudo guardar");
				return "/content-page/home";
	        }
		}

}
