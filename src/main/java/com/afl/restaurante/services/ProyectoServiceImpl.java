package com.afl.restaurante.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IProyectoDao;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Proyecto;

@Service
public class ProyectoServiceImpl implements IProyectoService {

	@Autowired
	IProyectoDao proyectoDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Proyecto> findAll (Pageable pageable) {
		return proyectoDao.findAll(pageable);
	}
	
	@Override
	@Transactional (readOnly = true)
	public Proyecto findById(Long id) {
		return proyectoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Proyecto save (Proyecto proyecto) {
		return proyectoDao.save(proyecto);
	}
	
	@Override
	@Transactional
	public void delete (Long id) {
		proyectoDao.deleteById(id);
	}

	@Override
	@Transactional (readOnly = true)
	public List<Proyecto> search(String cadena) {
		return proyectoDao.search(cadena);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Proyecto> findAll(Example<Proyecto> example, Pageable pageable) {
		return proyectoDao.findAll(example, pageable);
			
	}
}
	

