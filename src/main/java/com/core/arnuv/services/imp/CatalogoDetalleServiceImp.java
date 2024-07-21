package com.core.arnuv.services.imp;

import com.core.arnuv.model.CatalogoDetalle;
import com.core.arnuv.repository.ICatalogoDetalleRepository;
import com.core.arnuv.service.ICatalogoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class CatalogoDetalleServiceImp implements ICatalogoDetalleService {

	//@Autowired
	//private ICatalogoDetRepository repoCatalogo;

	@Autowired
	private ICatalogoDetalleRepository repo;

	@Override
	public List<CatalogoDetalle> listarCatalogoDetalle() {
		return repo.findAll();
	}

	@Override
	public CatalogoDetalle insertarCatalogoDetalle(CatalogoDetalle data) {
		return repo.save(data);
	}

	@Override
	public CatalogoDetalle actualizarCatalogoDetalle(CatalogoDetalle data) {
		//CatalogoDetalle existeCatalogo = repo.findById(data.getId()).orElse(null);
		///existeCatalogo.setNombre(data.getNombre());
		//existeCatalogo.setActivo(data.getActivo());
		return repo.save(data);
	}

	@Override
	public CatalogoDetalle buscarCatalogoDetalleId(int codigo) {
		return repo.findById(codigo).get();
	}
	
	@Override
	public void eliminarCatalogoDetalle(int codigo) {
		// TODO Auto-generated method stub
		try {
			repo.deleteById(codigo);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al eliminar item");
		}
	}
}
