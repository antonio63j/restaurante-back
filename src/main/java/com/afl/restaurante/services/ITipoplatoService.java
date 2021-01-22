package com.afl.restaurante.services;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Tipoplato;

public interface ITipoplatoService {
	

	public List<Tipoplato> findAllByNombreTipo();
	
	public Tipoplato findById(Long id);
	
	public Tipoplato save (Tipoplato tipoplato);
	
	void deleteById (Long id);
}
