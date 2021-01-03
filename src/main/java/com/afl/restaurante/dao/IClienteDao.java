package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Cliente;

public interface IClienteDao extends JpaRepository<Cliente, Long> {
	
    //@Query ("select c from Cliente c where c.actividad like %?1% or c.experiencia like %?1% order by inicio desc")
    @Query ("select c from Cliente c where lower(c.actividad) like lower(concat('%', ?1,'%')) or lower(c.experiencia) like lower(concat('%', ?1,'%')) order by inicio desc")

    public List<Cliente> search (String cadena);

}
