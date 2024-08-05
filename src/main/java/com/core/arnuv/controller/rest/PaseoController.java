package com.core.arnuv.controller.rest;

import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.response.UbicacionCabeceraResponse;
import com.core.arnuv.response.UbicacionDetalleResponse;
import com.core.arnuv.service.*;
import com.core.arnuv.utils.ArnuvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.core.arnuv.constants.Constants.KEY_RADIO;

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

	@Autowired
	private IUsuarioDetalleService userService;

	@GetMapping("/listar/{idpersona}")
	public ResponseEntity<?> listar(@PathVariable(value = "idpersona") Integer personId) {

		var listaUbicaciones = ubicacionService.listarUbicacion();

		Personadetalle personalogueada = personaDetalleService.buscarPorId(personId);
		
		var ubicacionPersonaLogueada = ubicacionService.ubicacionPersonaPorDefecto(personalogueada.getId());
		var perLatitud = ubicacionPersonaLogueada.getLatitud();
		var perLongitud =  ubicacionPersonaLogueada.getLongitud();
		
		UbicacionCabeceraResponse ubiCliente = new UbicacionCabeceraResponse();
		ubiCliente.setIdpersona(ubicacionPersonaLogueada.getIdpersona().getId());
		ubiCliente.setLatitud(ubicacionPersonaLogueada.getLatitud());
		ubiCliente.setLongitud(ubicacionPersonaLogueada.getLongitud());

		double radio = parametroService.getParametro(KEY_RADIO).getValorNumber();

		List<UbicacionDetalleResponse> ubiPaseadores = new ArrayList<>();

		for (Ubicacion ubicacion : listaUbicaciones) {
			
			double distancia = ArnuvUtils.distance(perLatitud,perLongitud, ubicacion.getLatitud(),
					ubicacion.getLongitud());
			if (distancia <= radio) {
				UbicacionDetalleResponse ubicacionresponce= new UbicacionDetalleResponse();
				ubicacionresponce.setIdpersona(ubicacion.getIdpersona().getId());
				ubicacionresponce.setLatitud(ubicacion.getLatitud());
				ubicacionresponce.setLongitud(ubicacion.getLongitud());
				ubiPaseadores.add(ubicacionresponce);
			}
		}

		ubiCliente.setUbicacionDetalleResponse(ubiPaseadores);
		return ResponseEntity.ok(ubiCliente);
	}

}
