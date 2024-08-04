package com.core.arnuv.services.imp;

import com.core.arnuv.model.Ubicacion;
import com.core.arnuv.repository.IUbicacionRepository;
import com.core.arnuv.service.IUbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service
@Component
public class UbicacionServiceImp implements IUbicacionService {
  @Autowired
    IUbicacionRepository repo;
  
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
	public Ubicacion ubicacionPaseadores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ubicacion ubicacionPersonaPorDefecto(int idpersona) {
		
		
		try {
            return repo.findByIsDefaultAndIdpersona(1, idpersona).orElseThrow();

        } catch (Exception e) {
            System.out.println("Error al Listar la Ubicacion");
        }
        return null;
		
	}
}
