package com.core.arnuv.repository;

import com.core.arnuv.model.Ubicacion;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {
	
	@Query("SELECT u FROM Ubicacion u WHERE u.idpersona.id=?2 AND u.isDefault=?1 ")
	Optional<Ubicacion> findByIsDefaultAndIdpersona(int isDefault,int idPersona);	
}
