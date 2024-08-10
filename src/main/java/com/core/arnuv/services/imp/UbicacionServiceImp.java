package com.core.arnuv.services.imp;

import com.core.arnuv.enums.RolEnum;
import com.core.arnuv.model.Personadetalle;
import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.model.Usuariorol;
import com.core.arnuv.repository.IUbicacionRepository;
import com.core.arnuv.repository.IUsuarioRolRepository;
import com.core.arnuv.service.IUbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Component
public class UbicacionServiceImp implements IUbicacionService {
  @Autowired
  private IUbicacionRepository repo;
  @Autowired
  private IUsuarioRolRepository repoRol;
  
    @Override
    public List<Ubicacion> listarUbicacion() {
        try {
            return repo.findAll();

        } catch (Exception e) {
            System.out.println("Error al Listar la Ubicacion");
        }
        return null;
    }

    @Override
    public Ubicacion insertarUbicacion(Ubicacion data) {
        try {
            return repo.save(data);

        } catch (Exception e) {
            System.out.println("Error al Insertar la Ubicacion");
        }

        return null;
    }

    @Override
    public Ubicacion actualizarUbicacion(Ubicacion data) {
        try {
            return repo.save(data);

        } catch (Exception e) {
            System.out.println("Error al Insertar la Ubicacion");
        }
        return null;
    }

    @Override
    public Ubicacion buscarPorId(int idUbicacion) {
        try{
            return repo.findById(idUbicacion).get();

        } catch (Exception e) {
            System.out.println("Error al Insertar Categoria");
            return null;
        }
    }

    @Override
    public void eliminarUbicacion(int idUbicacion) {
        try {
            repo.deleteById(idUbicacion);;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error al eliminar Ubicacion: " + e.getMessage());
        }
    }

	@Override
	public Set<Ubicacion> ubicacionPaseadores() {
        Set<Personadetalle> paseadores = repoRol.getAllByRolePerson(RolEnum.ROLE_WALKER.getDisplayName());
        return repo.getAllByListPersonId(paseadores.stream()
                .map(Personadetalle::getId)
                .collect(Collectors.toSet()), 1);
	}

	@Override
	public Ubicacion ubicacionPersonaPorDefecto(int idpersona) {
        return repo.findByIsDefaultAndIdpersona(1, idpersona)
                .orElseThrow(() -> new RuntimeException("No se encontro la ubicacion por defecto"));
	}
}
