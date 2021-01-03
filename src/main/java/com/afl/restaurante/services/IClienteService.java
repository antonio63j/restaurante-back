package com.afl.restaurante.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.afl.restaurante.entities.Cliente;
import com.afl.restaurante.entities.Curso;

public interface IClienteService {

	public Cliente findById(Long id);
	
	public List<Cliente> findAll ();
	
	public Page<Cliente> findAll(Pageable pageable);
	
	public List<Cliente> findAll(Sort sort);
	
	public List<Cliente> search(String term);
	
	public Cliente save (Cliente cliente);
	
	public void delete (Long id);
}
