																																																				package com.afl.restaurante.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.afl.restaurante.entities.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{

	// Hay dos formas de establecer las consultas: 1) por el nombre del método y 2) por medio de JPA query langue (JPQL)
	// Ejemplos en:
	// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	
	// 1)  A través del nombre del método (Query method name) se ejecutará la consulta JPQL:
	//     select u from Usuario u Where u.suername=?1
	public Usuario findByUsername (String username);
	
	// si quisieramos buscar por username y email sería:
	// public Cliente findByUsernameAndEmail (String username, String email);
	
	// 2) Con notación query  
	@Query ( "select u from Usuario u where u.username=?1")
	public Usuario findByUsernameQ (String username);
	
	// si quisieramos buscar por username y email sería:
	// @Query ( "select u from Usuario u where u.username=?1 and u.otro=?2")
	// public Cliente findByUsernameQ (String username, String email);
	
	public Usuario findByCodActivacion (String codActivacion);
	
	public boolean existsByUsername (String Username);
}
