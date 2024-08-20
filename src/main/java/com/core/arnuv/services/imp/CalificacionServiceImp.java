package com.core.arnuv.services.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Calificacion;
import com.core.arnuv.repository.ICalificacionRepository;
import com.core.arnuv.service.ICalificacionService;

@Service
@Component
public class CalificacionServiceImp implements ICalificacionService {

	@Autowired
	private ICalificacionRepository repo;
	@Override
	public List<Calificacion> listarCalificacion() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public List<Calificacion> BuscarPersonaPasedor(int idpersonapasedor) {
		// TODO Auto-generated method stub
		return repo.BuscarPersonaPasedor(idpersonapasedor);
	}

	@Override
	public Calificacion insertarCalificacion(Calificacion data) {
		// TODO Auto-generated method stub
		return repo.save(data);
	}

	@Override
	public Calificacion actualizarCalificacion(Calificacion data) {
		// TODO Auto-generated method stub
		return repo.save(data);
	}

	@Override
	public Calificacion buscarCalificacion(int id) {
		// TODO Auto-generated method stub
		return repo.findById(id).get();
	}

	@Override
	public void eliminarCalificacion(int id) {
		try {
			repo.deleteById(id);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al eliminar item");
		}
		
	}

	@Override
	public Calificacion findByIdpaseoId(int idpaseo) {
		// TODO Auto-generated method stub
		return repo.findByIdpaseoId(idpaseo);
	}

}
