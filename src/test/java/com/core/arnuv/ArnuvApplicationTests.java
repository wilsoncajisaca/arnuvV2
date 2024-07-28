package com.core.arnuv;

import com.core.arnuv.repository.IParametroRepository;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.utils.ArnuvUtils;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ArnuvApplicationTests {

	@Autowired
	private IParametroService service;
	@Test
	void contextLoads() {
		List<Usuario> usuarios = new ArrayList<>();
		usuarios.add(new Usuario("Juan", -34.6037, -58.3816)); // Buenos Aires
		usuarios.add(new Usuario("Maria", -34.9205, -57.9536)); // La Plata
		usuarios.add(new Usuario("Pedro", -32.9512, -60.6665)); // Rosario

		double miLatitud = -34.6037; // Tu latitud
		double miLongitud = -58.3816; // Tu longitud
		double radio = service.getParametro("RADIO").getValorNumber();

		List<Usuario> usuariosDentroDelRadio = new ArrayList<>();

		for (Usuario usuario : usuarios) {
			double distancia = ArnuvUtils.distance(miLatitud, miLongitud, usuario.getLatitud(), usuario.getLongitud());
			if (distancia <= radio) {
				usuariosDentroDelRadio.add(usuario);
			}
		}

		System.out.println("Usuarios dentro del radio de " + radio + " km:");
		for (Usuario usuario : usuariosDentroDelRadio) {
			System.out.println(usuario.getNombre());
		}
	}

	@Data
	public class Usuario {
		private String nombre;
		private double latitud;
		private double longitud;
		public Usuario(String nombre, double latitud, double longitud) {
			this.nombre = nombre;
			this.latitud = latitud;
			this.longitud = longitud;
		}
		}

}
