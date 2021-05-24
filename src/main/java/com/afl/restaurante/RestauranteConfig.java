package com.afl.restaurante;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.services.IEmpresaService;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
    
	
// Don't forget to change log level of org.springframework.web.filter.CommonsRequestLoggingFilter to DEBUG.
//	@Bean
//	public CommonsRequestLoggingFilter requestLoggingFilter() {
//		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
//		loggingFilter.setIncludeClientInfo(true);
//		loggingFilter.setIncludeQueryString(true);
//		loggingFilter.setIncludePayload(true);
//		loggingFilter.setMaxPayloadLength(64000);
//		return loggingFilter;
//
//	}

}
