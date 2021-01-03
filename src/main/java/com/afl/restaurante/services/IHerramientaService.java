package com.afl.restaurante.services;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.afl.restaurante.entities.Herramienta;

public interface IHerramientaService {

	public Herramienta findById(Long id);
	public List<Herramienta> findAllByNombre();
	public Page<Herramienta> findAll(Pageable page);
	public Page<Herramienta> findAll(Example<Herramienta> example, Pageable pageable);
	public Herramienta save (Herramienta herramiena);
	public void delete (Long id);
	public List<Herramienta> filtro (String cadenaBuscada);
	public List<Herramienta> search (String cadenaBuscada);
}
