package com.afl.restaurante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.services.IEmpresaService;

@Configuration
public class RestauranteConfig {
	
	@Autowired
	IEmpresaService empresaService;
	
	@Bean 
	public Empresa empresaStore() {
		Empresa empresa = empresaService.findById(new Long(1));
		if (empresa == null) {
			empresa = new Empresa();
		}
		return empresa;
	}

}
