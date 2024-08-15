package com.core.arnuv.services.imp;

import com.core.arnuv.model.Parametros;
import com.core.arnuv.repository.IParametroRepository;
import com.core.arnuv.service.IParametroService;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParametroService implements IParametroService {
    @Autowired
    private IParametroRepository repo;
    
    @Autowired
	private FirebaseFileService firebaseFileService;

    @Override
    public Parametros getParametro(String code) {
        return repo.findByCodigoAndEstado(code, true)
                .orElseThrow(() -> new RuntimeException("Parametro no encontrado"));
    }

    @Override
    public Parametros save(Parametros parametro) throws IOException  {
    	log.info("Service for create the sinister");
		String fileUrl = Strings.EMPTY;
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
}
