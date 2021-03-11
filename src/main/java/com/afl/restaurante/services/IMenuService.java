package com.afl.restaurante.services;

import java.util.List;
import java.util.Set;

import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.MenuSugerencia;

public interface IMenuService {

	public Set<Menu> findAllByLabel();
	
	public Set<Menu> findAllByLabelVisible(boolean visible);
	
	public Menu findById(Long id);
	
	public Menu save (Menu menu);
	
	void deleteById (Long id);
	
	void deleteMenuSugerenciaById (Long id);
	
	MenuSugerencia saveMenuSugerencia( MenuSugerencia menuSugerencia);
}
