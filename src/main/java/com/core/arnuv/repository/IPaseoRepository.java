package com.core.arnuv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.arnuv.model.Paseo;

@Repository
public interface IPaseoRepository extends JpaRepository<Paseo, Integer> {
	
	@Query("Select p from Paseo p where p.idpersonacliente.id= ?1")
    List<Paseo> buscarpersonacliente(int idcliente);
	
	@Query("Select p from Paseo p where p.idpersonapasedor.id= ?1")
    List<Paseo> buscaridpersonapasedor(int idpersonapasedor);

}
