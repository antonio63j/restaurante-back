package com.afl.restaurante.services;

import java.util.Set;
import com.afl.restaurante.entities.Sugerencia;

public interface ISugerenciaService {
	
	public Set<Sugerencia> findAllByLabel();
	
	public Sugerencia findById(Long id);
	
	public Sugerencia save (Sugerencia sugerencia);
	
	void deleteById (Long id);
	
	// void deleteMenuSugerenciaById (Long id);

}
