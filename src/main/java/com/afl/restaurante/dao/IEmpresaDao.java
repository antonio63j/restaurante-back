package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Empresa;

public interface IEmpresaDao extends JpaRepository<Empresa, Long> {
	
}
