package com.afl.restaurante.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.ITipoplatoDao;
import com.afl.restaurante.entities.Tipoplato;

@Service
public class TipoplatoSerivice implements ITipoplatoService {

	@Autowired ITipoplatoDao tipoplatoDao;
	
	@Override
	@Transactional (readOnly = true)
	public List<Tipoplato> findAllByNombreTipo() {
		
		return tipoplatoDao.findAllByNombreTipo();
	}

	@Override
	@Transactional (readOnly = true)
	public Tipoplato findById(Long id) {

		return tipoplatoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Tipoplato save(Tipoplato tipoplato) {
		
		return tipoplatoDao.save(tipoplato);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		
		tipoplatoDao.deleteById(id);
	}

}
