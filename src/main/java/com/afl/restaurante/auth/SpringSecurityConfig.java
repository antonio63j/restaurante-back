package com.afl.restaurante.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.afl.restaurante.services.UsuarioService;

//Para habilitar control de roles en controlador
@EnableGlobalMethodSecurity(securedEnabled = true) 
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);

	@Autowired
	private UserDetailsService usuarioService;

	// creamos bean pues se va a utilizar en la clase AuthorizationServerConfig
	@Bean
	public BCryptPasswordEncoder passwordEncoder () {
		logger.info("En BCryptPasswordEncoder");
		return new BCryptPasswordEncoder();
	}
	
	@Override
	@Autowired
	// inyectamos en auth el bean de la clase AuthenticationManagerBuilder
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder());
	}
	
	// Se utiliza en AuthorizationServerConfig
	@Bean ("authenticationManager")
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.anyRequest().authenticated()
		.and()
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
		
}
