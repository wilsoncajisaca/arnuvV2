package com.core.arnuv.services.imp;

import com.core.arnuv.model.Parametros;
import com.core.arnuv.repository.IParametroRepository;
import com.core.arnuv.service.IParametroService;
import com.core.arnuv.utils.ArnuvNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParametroService implements IParametroService {
    private final IParametroRepository repo;
	private final FirebaseFileService firebaseFileService;

    @Override
    public Parametros getParametro(String code) {
		return repo.findByCodigoAndEstado(code, Boolean.TRUE)
				.orElseThrow(() -> new ArnuvNotFoundException("Parametro para " + code + " no se encontro."));
    }

    @Override
    public Parametros save(Parametros parametro) throws IOException  {
    	log.info("Service for create the sinister");
		String fileUrl;
		if (parametro.getFile() != null && !parametro.getFile().isEmpty()) {
			fileUrl = firebaseFileService.saveFile(parametro.getFile());
			log.info("File url: " + fileUrl);
			parametro.setValorText(fileUrl);
			parametro.setEstado(Boolean.TRUE);
		}
        return repo.save(parametro);
    }

    @Override
    public Parametros delete(Parametros parametro) {
        parametro.setEstado(false);
        return repo.save(parametro);
    }

	@Override
	public Parametros findByCodigo(String code) {
		return repo.findByCodigo(code);
	}
}
