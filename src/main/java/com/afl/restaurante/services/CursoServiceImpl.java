package com.afl.restaurante.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.ICursoDao;
import com.afl.restaurante.entities.Curso;

@Service

public class CursoServiceImpl implements ICursoService{
    @Autowired ICursoDao cursoDao;
    
    @Transactional(readOnly = true)
    @Override
	public List<Curso> findAll() {
		return  (List<Curso>)cursoDao.findAll();
	}
    
	@Override
    @Transactional(readOnly = true)
	public Page<Curso> findAll(Pageable pageable) {
		return cursoDao.findAll(pageable);
	}
	
	@Override
    @Transactional(readOnly=true)
	public List<Curso> search(String term) {
		return cursoDao.findByNombreContainingIgnoreCaseOrderByInicioDesc(term);
	}

	@Override
	public List<Curso> findAll(Sort sort) {
		return cursoDao.findAll(sort);
	}
	
	@Override
	@Transactional
	public Curso save (Curso curso) {
		return cursoDao.save(curso);
	}
	
	@Override
	@Transactional
	public void delete (Long id) {
		cursoDao.deleteById(id);
	}

	@Override
	@Transactional
	public Curso findById (Long id) {
		return cursoDao.findById(id).orElse(null);
	}

}
