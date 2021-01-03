package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Herramienta;

public interface IHerramientaDao extends JpaRepository<Herramienta, Long> {
	
	// no se usa, queda como ejemplo
    public List<Herramienta> findByNombreContainingIgnoreCaseOrTipoOrComentarioContainingIgnoreCase(String searchTerm, String searcTerm, String searhTerm);
    
    @Query ("select h from Herramienta h where lower(h.nombre) like lower(concat('%', ?1,'%')) or "
    		+ "lower(h.tipo) like lower(concat('%', ?1,'%')) or "
    		+ "lower(h.comentario) like lower(concat('%', ?1,'%')) order by nombre asc")
    public List<Herramienta> search (String cadena);

}
