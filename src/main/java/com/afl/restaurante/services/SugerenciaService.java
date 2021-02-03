package com.afl.restaurante.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IMenuSugerenciaDao;
import com.afl.restaurante.dao.ISugerenciaDao;
import com.afl.restaurante.entities.MenuSugerencia;
import com.afl.restaurante.entities.Sugerencia;

@Service
public class SugerenciaService implements ISugerenciaService {
	
	@Autowired
	private ISugerenciaDao sugerenciaDao;
	
	@Autowired
	private IMenuSugerenciaDao menuSugerenciaDao;

	@Override
	@Transactional (readOnly = true)
	public Set<Sugerencia> findAllByLabel() {
		return sugerenciaDao.findAllByLabel();
	}

	@Override
	@Transactional (readOnly = true)
	public Sugerencia findById(Long id) {
		return sugerenciaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Sugerencia save(Sugerencia sugerencia) {
		return sugerenciaDao.save(sugerencia);
	}
	
	@Override
	@Transactional
	public void deleteById (Long id) {
		
		// eliminar entradas en tabla MenuSugerencia
		
		List<MenuSugerencia> menuSugerencias = menuSugerenciaDao.findMenuSugerencias(id);
		menuSugerenciaDao.deleteAll(menuSugerencias);
		sugerenciaDao.deleteById(id);
	}

//	@Override
//	@Transactional
//	public void deleteMenuSugerenciaById(Long id) {
//		menuSugerenciaDao.deleteById(id);
//
//	}

}
