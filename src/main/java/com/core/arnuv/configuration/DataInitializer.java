package com.core.arnuv.configuration;

import com.core.arnuv.enums.RolEnum;
import com.core.arnuv.model.*;
import com.core.arnuv.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final IPersonaDetalleRepository personaDetalleRepository;
    private final IUsuarioDetalleRepository usuarioDetalleRepository;
    private final IRolRepository rolRepository;
    private final IUsuarioRolRepository usuariorolRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initMenu() {
        try {
            List<Rol> roles = rolRepository.findAll();
            if(!CollectionUtils.isEmpty(roles)) return;

            Personadetalle personaEnt = personaDetalleRepository.buscarPorIdentificacion("0105022248");
            if(Objects.nonNull(personaEnt)) return;

            String sql = new String(Files.readAllBytes(Paths.get(new ClassPathResource("schema.sql").getURI())));
            jdbcTemplate.execute(sql);
            this.init();
            System.out.println("✅Script SQL ejecutado correctamente después de que la aplicación esté lista.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para insertar datos iniciales
     */
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
            usuario.setEstado(true);
            usuarioDetalleRepository.save(usuario);

            Rol rol = rolRepository.findByNombre(RolEnum.ROLE_ADMIN.getValue());

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
            log.error("Error al insertar datos iniciales: {}", e.getMessage());
        }
    }
}