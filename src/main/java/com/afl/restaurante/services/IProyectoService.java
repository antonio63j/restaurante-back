package com.afl.restaurante.services;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Proyecto;

public interface IProyectoService {

	public Page<Proyecto> findAll (Pageable pageable);
	public Proyecto findById(Long id);
	public Proyecto save (Proyecto proyecto);
	public void delete (Long id);
	public List<Proyecto> search (String cadena);
	public Page<Proyecto> findAll(Example<Proyecto> example, Pageable pageable);
}
