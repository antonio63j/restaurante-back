package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Proyecto;

public interface IProyectoDao extends JpaRepository<Proyecto, Long> {
	
	public List<Proyecto> findByNombreContainingIgnoreCase(String term);
	
	//@Query ("select p from Proyecto p where p.nombre like %?1% or p.empresa like %?1% or p.descripcion like %?1% order by inicio desc")
	@Query ("select p from Proyecto p where lower(p.nombre) like lower(concat('%', ?1,'%')) or lower(p.descripcion) like lower(concat('%', ?1, '%')) order by inicio desc")
	public List<Proyecto> search (String cadena);

}
