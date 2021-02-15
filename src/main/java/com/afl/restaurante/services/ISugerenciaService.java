package com.afl.restaurante.services;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.afl.restaurante.entities.Sugerencia;

public interface ISugerenciaService {
	
	public Page<Sugerencia> findAll(Pageable pageable);
	
	public Set<Sugerencia> findAllByLabel();
	
	public Sugerencia findById(Long id);
	
	public Sugerencia save (Sugerencia sugerencia);
	
	void deleteById (Long id);
	
	// void deleteMenuSugerenciaById (Long id);
	
	public Page<Sugerencia> findAll (Specification<Sugerencia> especification, Pageable pageable);

}
