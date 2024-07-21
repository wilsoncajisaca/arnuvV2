package com.core.arnuv.services.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.repository.IMascotaDetalleRepository;
import com.core.arnuv.service.IMascotaDetalleService;

@Service
@Component
public class MascotaDetalleServiceImp implements IMascotaDetalleService {

	@Autowired
	private IMascotaDetalleRepository repo;

	@Override
	public List<MascotaDetalle> listarMascotasDetalle() {
		return repo.findAll();
	}

	@Override
	public MascotaDetalle insertarMascotaDetalle(MascotaDetalle data) {
		return repo.save(data);
	}

	@Override
	public MascotaDetalle actualizarMascotaDetalle(MascotaDetalle data) {
		//MascotaDetalle existeMascota = repo.findById(data.getId()).orElse(null);
		//existeMascota.setNombre(data.getNombre());
		//existeMascota.setEdad(data.getEdad());
		return repo.save(data);
	}
	
	@Override
	public void EliminarMascotaDetalle(int codigo) {
		// TODO Auto-generated method stub
		try {
			repo.deleteById(codigo);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al eliminar item");
		}
	}

	@Override
	public MascotaDetalle buscarMascotaID(int codigo) {
		return repo.findById(codigo).get();
	}
}
