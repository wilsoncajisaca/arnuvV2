package com.core.arnuv.services.imp;

import com.core.arnuv.enums.RolEnum;
import com.core.arnuv.model.*;
import com.core.arnuv.repository.IPersonaDetalleRepository;
import com.core.arnuv.request.PersonaDetalleRequest;
import com.core.arnuv.request.UsuarioDetalleRequest;
import com.core.arnuv.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class PersonaDetalleServiceImp implements IPersonaDetalleService {
	private final IPersonaDetalleRepository repo;
	private final IUbicacionService ubicacionService;
	private final PasswordEncoder passwordEncoder;
	private final IUsuarioDetalleService userService;
	private final IRolService servicioRol;
	private final IUsuarioRolService servicioUsuarioRol;
	
	@Override
	public List<Personadetalle> listarTodosPersonaDetalle() {
		return repo.findAll();
	}

	@Override
	public Personadetalle insertarPersonaDetalle(Personadetalle data) {
		return repo.save(data);
	}

	@Override
	public Personadetalle actualizarPersonaDetalle(Personadetalle data) {
		Personadetalle existePersonaDetalle= repo.findById(data.getId()).orElse(null);
		existePersonaDetalle.setIdusuarioing(data.getIdusuarioing());
		existePersonaDetalle.setIdusuariomod(data.getIdusuariomod());
		existePersonaDetalle.setFechaingreso(data.getFechaingreso());
		existePersonaDetalle.setFechamodificacion(data.getFechamodificacion());
		existePersonaDetalle.setNombres(data.getNombres());
		existePersonaDetalle.setApellidos(data.getApellidos());
		existePersonaDetalle.setIdentificacion(data.getIdentificacion());
		existePersonaDetalle.setCelular(data.getCelular());
		existePersonaDetalle.setEmail(data.getEmail());
		return repo.save(existePersonaDetalle);
	}

	@Override
	public Personadetalle buscarPorId(int id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public Personadetalle buscarPorIdentificacion(String identificacion) {
		return repo.buscarPorIdentificacion(identificacion);
	}

	@Override
	public boolean eliminar(Personadetalle data) {
		repo.delete(data);
		return true;
	}

	@Override
	public Personadetalle buscarEmail(String email) {
		return repo.buscarEmail(email);
	}
	@Override
	public String verificarDuplicados(String email, String celular, String identificacion) {
		String error = Strings.EMPTY;
		if(buscarEmail(email)!=null){
			error ="El correo electronico ya se encuentra registrado";
		}
		if(buscarPorIdentificacion(identificacion) != null){
			error ="La identificación ya se encuentra registrada";
		}
		if (buscarPorCelular(celular) != null) {
			error ="El celular ya se encuentra registrado";
		}
		return error;
	}

	@Override
	public Personadetalle buscarPorCelular(String celular) {
		return repo.findByCelular(celular);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Personadetalle guardarInformacionCompleta(PersonaDetalleRequest persona, UsuarioDetalleRequest usuario) throws Exception {
		var personaEnt = new Personadetalle();
		try {
			personaEnt = insertarPersonaDetalle(persona.mapearDato(persona, Personadetalle.class, "idcatalogoidentificacion", "iddetalleidentificacion"));
			crearUbicacion(persona, personaEnt);
			crearUsuario(personaEnt, usuario);
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return personaEnt;
	}

	private void crearUbicacion(PersonaDetalleRequest persona, Personadetalle personaEnt) {
		var ubicacion = new Ubicacion();
		ubicacion.setLatitud(persona.getLatitud());
		ubicacion.setLongitud(persona.getLongitud());
		ubicacion.setIsDefault(1);
		ubicacion.setIdpersona(personaEnt);
		ubicacionService.insertarUbicacion(ubicacion);
	}

	private void crearUsuario(Personadetalle persona, UsuarioDetalleRequest usuario) throws Exception {
		Usuariodetalle usuariodetalle = usuario.mapearDato(usuario, Usuariodetalle.class);
		usuariodetalle.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuariodetalle.setEstado(true);
		usuariodetalle.setIdpersona(persona);
		if(userService.buscarPorEmailOrUserName(usuario.getUsername()) != null) {
			throw new Exception("El nombre de usuario ya se encuentra registrado.");
		}
		Usuariodetalle usuarioEnt = userService.insertarUsuarioDetalle(usuariodetalle);
		crearRol(usuarioEnt, RolEnum.ROLE_USER);
	}

	private void crearRol(Usuariodetalle usuario, RolEnum role) {
		var rolentity = servicioRol.findByNombre(role.getDisplayName());
		UsuariorolId usuariorolId = new UsuariorolId();
		usuariorolId.setIdusuario(usuario.getIdusuario());
		usuariorolId.setIdrol(rolentity.getId());
		Usuariorol usuariorol = new Usuariorol();
		usuariorol.setIdrol(rolentity);
		usuariorol.setIdusuario(usuario);
		usuariorol.setId(usuariorolId);
		servicioUsuarioRol.insertarUsuarioRol(usuariorol);
	}
}

