package com.afl.restaurante.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IHerramientaDao;
import com.afl.restaurante.entities.Herramienta;

@Service
public class HerramientaServiceImpl implements IHerramientaService{

	@Autowired 
	private IHerramientaDao herramientaDao;
	
	@Override
	@Transactional(readOnly = true)
	public Herramienta findById(Long id) {
		return herramientaDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Herramienta> findAllByNombre() {
		return herramientaDao.findAll(Sort.by("nombre").ascending());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Herramienta> findAll(Pageable page) {
		return herramientaDao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Herramienta> findAll(Example<Herramienta> example, Pageable pageable) {
			return herramientaDao.findAll(example, pageable);
		
	}
	
	@Override
	@Transactional
	public Herramienta save (Herramienta herramienta) {
		return herramientaDao.save(herramienta);
	}
	
	@Override
	@Transactional
    public void delete (Long id) {
		herramientaDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Herramienta> filtro(String strBusqueda) {
		return herramientaDao.findByNombreContainingIgnoreCaseOrTipoOrComentarioContainingIgnoreCase(strBusqueda, strBusqueda, strBusqueda);
	}

	@Override
	public List<Herramienta> search(String cadenaBuscada) {
		return herramientaDao.search(cadenaBuscada);
	}
	
}
