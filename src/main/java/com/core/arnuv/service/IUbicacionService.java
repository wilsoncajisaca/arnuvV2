package com.core.arnuv.service;


import com.core.arnuv.model.Ubicacion;

import java.util.List;

public interface IUbicacionService  {
    List<Ubicacion> listarUbicacion();

    public Ubicacion insertarUbicacion(Ubicacion data);

    public Ubicacion actualizarUbicacion(Ubicacion data);

    public Ubicacion buscarPorId(int idUbicacion);

    public void eliminarUbicacion(int idUbicacion);
}
