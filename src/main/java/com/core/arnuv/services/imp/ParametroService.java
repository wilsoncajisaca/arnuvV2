package com.core.arnuv.services.imp;

import com.core.arnuv.model.Parametros;
import com.core.arnuv.repository.IParametroRepository;
import com.core.arnuv.service.IParametroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametroService implements IParametroService {
    @Autowired
    private IParametroRepository repo;

    @Override
    public Parametros getParametro(String code) {
        return repo.findByCodigoAndEstado(code, true)
                .orElseThrow(() -> new RuntimeException("Parametro no encontrado"));
    }

    @Override
    public Parametros save(Parametros parametro) {
        return repo.save(parametro);
    }

    @Override
    public Parametros delete(Parametros parametro) {
        parametro.setEstado(false);
        return repo.save(parametro);
    }
}
