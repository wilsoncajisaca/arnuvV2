package com.core.arnuv.services.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.Paseo;
import com.core.arnuv.repository.IPaseoRepository;
import com.core.arnuv.service.IPaseoService;

@Service
@Component
public class PaseoServiceImp implements IPaseoService {

	@Autowired
	private IPaseoRepository repo;

	@Override
	public List<Paseo> listarPaseos() {
		return repo.findAll();
	}

	@Override
	public Paseo insertarPaseo(Paseo data) {
		return repo.save(data);
	}

	@Override
	public Paseo actualizarPaseo(Paseo data) {
		/*Paseo existePaseo = repo.findById(data.getIdpaseo()).orElse(null);
		existePaseo.setFecharealinicio(data.getFecharealinicio());
		existePaseo.setFecharealfin(data.getFecharealfin());
		existePaseo.setFechainicio(data.getFechainicio());
		existePaseo.setFechafin(data.getFechafin());
		existePaseo.setHorainicio(data.getHorainicio());
		existePaseo.setHorafin(data.getHorafin());
		existePaseo.setPreciototal(data.getPreciototal());
		existePaseo.setObservacionpaseo(data.getObservacionpaseo());
		existePaseo.setObservacionpaseador(data.getObservacionpaseador());*/
		return repo.save(data);
	}

	@Override
	public Paseo buscarPorId(int codigo) {
		return repo.findById(codigo).get();
	}
	
	@Override
	public void eliminarPaseo(int codigo) {
		// TODO Auto-generated method stub
		try {
			repo.deleteById(codigo);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al eliminar item");
		}
	}
	
}
