package com.core.arnuv.service;

import com.core.arnuv.model.Parametros;

public interface IParametroService {
    Parametros getParametro(String code);

    Parametros save(Parametros parametro);

    Parametros delete(Parametros parametro);
}
