package com.core.arnuv.configuration;

import com.core.arnuv.model.*;
import com.core.arnuv.repository.IPersonaDetalleRepository;
import com.core.arnuv.repository.IRolRepository;
import com.core.arnuv.repository.IUsuarioDetalleRepository;
import com.core.arnuv.repository.IUsuarioRolRepository;
import com.core.arnuv.service.IUsuarioDetalleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final IPersonaDetalleRepository personaDetalleRepository;
    private final IUsuarioDetalleRepository usuarioDetalleRepository;
    private final IRolRepository rolRepository;
    private final IUsuarioRolRepository usuariorolRepository;
    private final IUsuarioDetalleService servicioUsuarioDetalle;

    @PostConstruct
    public void init() {
        try {
            Personadetalle persona = new Personadetalle();
            persona.setApellidos("admin");
            persona.setCelular("0967325098");
            persona.setEmail("admin@gmail.com");
            persona.setIdentificacion("0105022248");
            persona.setNombres("admin");
            personaDetalleRepository.save(persona);

            Usuariodetalle usuario = new Usuariodetalle();
            usuario.setPassword("$2a$10$24gd1zfug.iQ5Z7Tv6tSduXt9iZCc5V2LFgJn0jbFriseX0dpnpiS");
            usuario.setUsername("admin");
            usuario.setIdpersona(persona);
            usuarioDetalleRepository.save(usuario);

            // Insertar en la tabla 'rol'
            Rol rol = new Rol();
            rol.setNombre("ADMIN");
            rolRepository.save(rol);

            UsuariorolId usuariorolId = new UsuariorolId();
            usuariorolId.setIdusuario(usuario.getIdusuario());
            usuariorolId.setIdrol(rol.getId());
            Usuariorol usuariorol = new Usuariorol();
            usuariorol.setIdrol(rol);
            usuariorol.setIdusuario(usuario);
            usuariorol.setId(usuariorolId);
            usuariorolRepository.save(usuariorol);

            log.info("Datos iniciales insertados correctamente.");
        } catch (Exception e) {
            System.err.println("Error al insertar datos iniciales: " + e.getMessage());
        }
    }
}