package com.core.arnuv.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.core.arnuv.model.Paseo;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.UbicacionResponse;
import com.core.arnuv.service.IMascotaDetalleService;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.service.IPaseoService;
import com.core.arnuv.service.IPersonaDetalleService;
import com.core.arnuv.service.ITarifarioService;
import com.core.arnuv.service.IUbicacionService;
import com.core.arnuv.utils.ArnuvUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/paseos")
public class PaseoController {

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
	public ResponseEntity<?> listar(HttpServletRequest request) {

		var listaUbicaciones = ubicacionService.listarUbicacion();

		Personadetalle personalogueada = personaDetalleService.buscarPorId(4);//ArnuvUtils.getUserInSession(request);
		
		var ubicacionPersonaLogueada = ubicacionService.ubicacionPersonaPorDefecto(personalogueada.getId());
		var perLatitud = ubicacionPersonaLogueada.getLatitud();
		var perLongitud =  ubicacionPersonaLogueada.getLongitud();
		
		
		double radio = parametroService.getParametro("RADIO").getValorNumber();

		List<UbicacionResponse> ubiPaseadores = new ArrayList<>();

		for (Ubicacion ubicacion : listaUbicaciones) {
			
			double distancia = ArnuvUtils.distance(perLatitud,perLongitud, ubicacion.getLatitud(),
					ubicacion.getLongitud());
			if (distancia <= radio) {
				UbicacionResponse ubicacionresponce= new UbicacionResponse();
				ubicacionresponce.setIdpersona(ubicacion.getIdpersona().getId());
				ubicacionresponce.setLatitud(ubicacion.getLatitud());
				ubicacionresponce.setLongitud(ubicacion.getLongitud());
				ubiPaseadores.add(ubicacionresponce);
			}
		}

		return ResponseEntity.ok(ubiPaseadores);
	}

}
