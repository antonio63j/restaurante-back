package com.afl.restaurante.services;

import com.afl.restaurante.entities.Empresa;

public interface IEmpresaService {

	public Empresa findById(Long id);
	
	// public Empresa create (Empresa empresa);
	
	public Empresa save (Empresa empresa);
}
