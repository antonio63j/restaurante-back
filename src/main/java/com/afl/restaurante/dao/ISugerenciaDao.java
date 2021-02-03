package com.afl.restaurante.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.afl.restaurante.entities.Sugerencia;

public interface ISugerenciaDao extends JpaRepository<Sugerencia, Long> {
	
    @Query ("select s from Sugerencia s order by s.label asc")
    public Set<Sugerencia> findAllByLabel();

}
