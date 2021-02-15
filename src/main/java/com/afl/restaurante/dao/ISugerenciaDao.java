package com.afl.restaurante.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Sugerencia;

public interface ISugerenciaDao extends JpaRepository<Sugerencia, Long>, JpaSpecificationExecutor<Sugerencia> {
	
    @Query ("select s from Sugerencia s order by s.label asc")
    public Set<Sugerencia> findAllByLabel();
    
    // en pruebas
//    @Query ("select c from Cliente c where lower(c.actividad) like lower(concat('%', ?1,'%')) or lower(c.experiencia) like lower(concat('%', ?1,'%')) order by inicio desc")
//
//    public List<Cliente> search (String cadena);

    // 

}
