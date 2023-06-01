package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Tipoplato;

public interface ITipoplatoDao extends JpaRepository<Tipoplato, Long> {

    @Query ("select p from Tipoplato p order by p.nombre asc")
    public List<Tipoplato> findAllByNombreTipo();

	// List<Tipoplato> findAllByNombre();
	
}