package com.core.arnuv.controller;

import com.core.arnuv.model.Parametros;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.service.IParametroService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static com.core.arnuv.constants.Constants.KEY_PLANTILLA_MAIL;
import static com.core.arnuv.constants.Constants.KEY_RADIO;

@Controller
@RequiredArgsConstructor
@RequestMapping("parametro")
public class ParametroController {
	
	private final IParametroService  parametroService;
	
	@GetMapping("/general")
	@PreAuthorize("hasRole('ADMIN')")
	public String general(Model model) {	 
		return "content-page/parametro";
	}

	@GetMapping("/nuevaImagen")
	@PreAuthorize("hasRole('ADMIN')")
	public String crearImagen(Model model) {
		model.addAttribute("nuevo", new Parametros());	 
		return "content-page/parametro-subir-imagen";
	}

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
			return "content-page/home";
		}
	}

	@GetMapping("/crearParametrosPlantillaMaill")
	@PreAuthorize("hasRole('ADMIN')")
	public String crearParametrosPlantillaMaill(Model model) {
		model.addAttribute("nuevo", new PersonaDetalleRequest());
		return "content-page/parametro-subir-plantilla";
	}

	@PostMapping("/subirArchivoPlantillaMaill")
	@PreAuthorize("hasRole('ADMIN')")
	public String subirArchivosPlantillaMaill(@RequestParam("archivo") MultipartFile archivo) {
		try {
			Parametros doc = new Parametros();

			System.out.println(KEY_PLANTILLA_MAIL);

			doc.setCodigo(KEY_PLANTILLA_MAIL);
			doc.setArchivos(archivo.getBytes());
			doc.setEstado(Boolean.TRUE);
			Parametros parametroPlantilla = parametroService.findByCodigo(KEY_PLANTILLA_MAIL);

			if (parametroPlantilla != null && parametroPlantilla.getId() != null) {

				doc.setId(parametroPlantilla.getId());
			}

			parametroService.save(doc);
			return "redirect:/home";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/home";
		}
	}

	@GetMapping("/crearParametrosRadio")
	@PreAuthorize("hasRole('ADMIN')")
	public String crearParametrosRadio(Model model) {
		model.addAttribute("nuevo", new Parametros());
		return "content-page/parametros-radio";
	}

	@PostMapping("/modificarParametrosRadio")
	@PreAuthorize("hasRole('ADMIN')")
	public String modificarParametrosRadio(@ModelAttribute("nuevo") Parametros nuevo) {
		try {
			Parametros doc = new Parametros();
			doc.setCodigo(KEY_RADIO);
			doc.setValorNumber(nuevo.getValorNumber());
			doc.setEstado(Boolean.TRUE);
			Parametros parametroPlantilla = parametroService.getParametro(KEY_RADIO);
			if (parametroPlantilla != null && parametroPlantilla.getId() != null) {
				doc.setId(parametroPlantilla.getId());
			}
			parametroService.save(doc);
			return "redirect:/home";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/home";
		}
	}
}
